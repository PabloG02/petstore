package pablog.petstore.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Logger;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.Status.VALID;

/**
 * Single authentication mechanism for the EAR: Basic auth for /api/**, form auth for JSF pages.
 */
@ApplicationScoped
public class HybridAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final String API_PREFIX = "/api/";
    private static final String BASIC_CHALLENGE = "Basic realm=\"PetStoreRealm\"";
    private static final String LOGIN_PAGE = "/login.xhtml";

    @Inject
    IdentityStoreHandler identityStoreHandler;

    private static final Logger LOG = Logger.getLogger(HybridAuthenticationMechanism.class.getName());

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpMessageContext context) {
        LOG.info(String.format("validateRequest - URI: %s, Principal: %s, Method: %s",
                request.getRequestURI(),
                context.getCallerPrincipal(),
                request.getMethod()));

        // Let CORS preflight through without auth.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return context.doNothing();
        }

        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        boolean apiRequest = requestUri.startsWith(contextPath + API_PREFIX);

        if (apiRequest) {
            return handleBasic(request, response, context);
        }

        return handleForm(request, context);
    }

    private AuthenticationStatus handleBasic(HttpServletRequest request,
                                             HttpServletResponse response,
                                             HttpMessageContext context) {
        // If already authenticated (e.g., via form login), allow the request.
        if (context.getCallerPrincipal() != null) {
            return context.notifyContainerAboutLogin(context.getCallerPrincipal(), context.getGroups());
        }

        String authHeader = request.getHeader("Authorization");
        Optional<UsernamePasswordCredential> credential = parseBasicHeader(authHeader);

        if (credential.isEmpty()) {
            response.setHeader("WWW-Authenticate", BASIC_CHALLENGE);
            return context.responseUnauthorized();
        }

        CredentialValidationResult result = identityStoreHandler.validate(credential.get());
        if (result.getStatus() == VALID) {
            return context.notifyContainerAboutLogin(result);
        }

        response.setHeader("WWW-Authenticate", BASIC_CHALLENGE);
        return context.responseUnauthorized();
    }

    private AuthenticationStatus handleForm(HttpServletRequest request, HttpMessageContext context) {
        // CRITICAL: Check for existing authenticated principal from session FIRST
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            LOG.info(String.format("Found existing principal in session: %s", principal.getName()));
            // User is already authenticated via session, just inform the container
            return context.notifyContainerAboutLogin(principal, context.getGroups());
        }

        // JSF login flows call SecurityContext.authenticate with a UsernamePasswordCredential.
        Credential incoming = context.getAuthParameters().getCredential();
        if (incoming instanceof UsernamePasswordCredential upc) {
            CredentialValidationResult result = identityStoreHandler.validate(upc);
            if (result.getStatus() == VALID) {
                LOG.info(String.format("Form login successful for user: %s", result.getCallerPrincipal().getName()));

                // Register the session
                context.setRegisterSession(result.getCallerPrincipal().getName(), result.getCallerGroups());

                return context.notifyContainerAboutLogin(
                        result.getCallerPrincipal(),
                        result.getCallerGroups()
                );
            }
            LOG.info("Credential validation failed");
            return context.responseUnauthorized();
        }

        // Redirect unauthenticated users hitting protected JSF pages to the login page.
        if (context.isProtected() && !isLoginRequest(request)) {
            LOG.info("Redirecting unauthenticated user to login page");
            return context.redirect(request.getContextPath() + LOGIN_PAGE);
        }

        return context.doNothing();
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        return uri.equals(contextPath + LOGIN_PAGE) || uri.equals(contextPath + LOGIN_PAGE + "?faces-redirect=true");
    }

    private Optional<UsernamePasswordCredential> parseBasicHeader(String authHeader) {
        if (authHeader == null || !authHeader.regionMatches(true, 0, "Basic ", 0, 6)) {
            return Optional.empty();
        }

        String base64 = authHeader.substring(6).trim();
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }

        String token = new String(decoded, StandardCharsets.UTF_8);
        int colon = token.indexOf(':');
        if (colon < 0) {
            return Optional.empty();
        }

        String username = token.substring(0, colon);
        String password = token.substring(colon + 1);
        return Optional.of(new UsernamePasswordCredential(username, password));
    }
}

