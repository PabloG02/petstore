package pablog.petstore.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SecurityExceptionMapper implements ExceptionMapper<SecurityException> {
    @Override
    public Response toResponse(SecurityException e) {
        return Response.status(Response.Status.FORBIDDEN)
				.entity(e.getMessage())
				.type(MediaType.TEXT_PLAIN)
				.build();
    }
}
