package pablog.petstore.jsf.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.logging.Logger;

@Named
@RequestScoped
public class LoginManagedBean {

    private static final Logger log = Logger.getLogger(LoginManagedBean.class.getName());

    @Inject
    private SecurityContext securityContext;

    private String username;
    private String password;

    public String login() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

        // Ensure session exists before authentication
        request.getSession(true);

        Credential credential = new UsernamePasswordCredential(username, new Password(password));

        try {
            AuthenticationStatus status = securityContext.authenticate(
                    request,
                    response,
                    AuthenticationParameters.withParams().credential(credential)
            );

            log.info(String.format("AuthenticationStatus for user '%s': %s", username, status));

            switch (status) {
                case SEND_CONTINUE:
                    log.info("Container will handle authentication (redirect/forward). Stopping JSF lifecycle.");
                    facesContext.responseComplete();
                    return null;
                case SEND_FAILURE:
                    log.info(String.format("Authentication failed for user '%s'. Possible reasons: bad credentials, account locked, etc.", username));
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Login Failed", "Invalid credentials or account issue"));
                    return null;
                case SUCCESS:
                    log.info(String.format("User '%s' successfully authenticated.", username));
                    return "/index.xhtml?faces-redirect=true";
                default:
                    log.info(String.format("Unexpected AuthenticationStatus '%s' for user '%s'", status, username));
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Login Failed", "Unexpected status: " + status));
                    return null;
            }
        } catch (Exception e) {
            log.info(String.format("Unexpected exception during authentication for user '%s': %s", username, e.getMessage()));
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Login Failed", "Unexpected error: " + e.getMessage()));
            return null;
        }
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            request.logout();
            request.getSession().invalidate();
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Logout Failed", e.getMessage()));
        }

        return "/index.xhtml?faces-redirect=true";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
