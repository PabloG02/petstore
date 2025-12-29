package pablog.petstore.rest;

import pablog.petstore.domain.entities.User;
import pablog.petstore.rest.entity.UserCredentials;
import pablog.petstore.service.UserService;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @EJB
    private UserService service;

    @GET
    public Response getCredentials() {
        try {
            final User currentUser = this.service.getCurrentUser();

            return Response.ok(new UserCredentials(currentUser)).build();
        } catch (EJBAccessException eae) {
            throw new SecurityException(eae);
        }
    }
}
