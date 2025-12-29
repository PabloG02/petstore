package pablog.petstore.rest;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pablog.petstore.domain.entities.Pet;
import pablog.petstore.rest.entity.PetData;
import pablog.petstore.service.PetService;

import java.net.URI;

/**
 * Resource that represents the pets in the application.
 *
 * @author Miguel Reboiro Jato
 */
@Path("pets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PetResource {
    @EJB
    private PetService service;

    @Context
    private UriInfo uriInfo;

    /**
     * Returns the owner identified by the login.
     *
     * @param id the identified of a pet.
     * @return an {@code OK} response containing the {@link Pet} with the
     * provided identifier.
     * @throws SecurityException if the current owner does not own the pet.
     */
    @Path("{id}")
    @GET
    public Response get(@PathParam("id") int id) throws SecurityException {
        try {
            final Pet pet = this.service.get(id);

            if (pet == null) throw new IllegalArgumentException("Pet not found: " + id);
            else return Response.ok(pet).build();
        } catch (EJBAccessException eae) {
            throw new SecurityException(eae);
        }
    }

    /**
     * Returns the complete list of pets of the current owner.
     *
     * @return an {@code OK} response containing the complete list of pets of
     * the current owner.
     */
    @GET
    public Response list() {
        return Response.ok(this.service.list()).build();
    }

    /**
     * Creates a new pet owned by the current user.
     *
     * @param petData a new owner to be stored.
     * @return a {@code CREATED} response with the URI of the new pet in the
     * {@code Location} header.
     * @throws IllegalArgumentException if pet is {@code null} or if a pet with
     *                                  the same identifier already exists.
     * @throws SecurityException        if the pet already has an owner, and it
     *                                  is not the current user. If the pet has no
     *                                  owner, this exception will never be thrown.
     */
    @POST
    public Response create(PetData petData) throws SecurityException {
        if (petData == null) throw new IllegalArgumentException("pet can't be null");

        try {
            final Pet pet = this.service.create(petData.toPet());

            final URI petUri = uriInfo.getAbsolutePathBuilder().path(Integer.toString(pet.getId())).build();

            return Response.created(petUri).build();
        } catch (EntityExistsException eee) {
            throw new IllegalArgumentException("The pet already exists");
        } catch (EJBAccessException eae) {
            throw new SecurityException(eae);
        }
    }

    /**
     * Updates the information of a pet. If the pet is not stored, it will be created.
     *
     * @param id      the identifier of the pet to be modified.
     * @param petData a pet to be updated.
     * @return an empty {@code OK} response.
     * @throws IllegalArgumentException if pet is {@code null} of it has no owner.
     * @throws SecurityException        if the pet's owner is not the current user.
     */
    @Path("{id}")
    @PUT
    public Response update(@PathParam("id") int id, PetData petData) throws SecurityException {
        if (petData == null) throw new IllegalArgumentException("pet can't be null");

        try {
            final Pet pet = this.service.get(id);
            petData.assignData(pet);

            this.service.update(pet);

            return Response.ok().build();
        } catch (EJBAccessException eae) {
            throw new SecurityException(eae);
        }
    }

    /**
     * Deletes a pet.
     *
     * @param id the identifier of the pet to be deleted.
     * @return an empty {@code OK} response.
     * @throws IllegalArgumentException if there is no pet with the provided identifier.
     * @throws SecurityException        if the pet's owner is not the current user.
     */
    @Path("{id}")
    @DELETE
    public Response delete(@PathParam("id") int id) throws SecurityException {
        try {
            this.service.remove(id);

            return Response.ok().build();
        } catch (EJBAccessException eae) {
            throw new SecurityException(eae);
        }
    }
}
