package pablog.petstore.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pablog.petstore.domain.entities.User;

import java.security.Principal;

@Stateless
@RolesAllowed({"OWNER", "ADMIN"})
public class UserService {
    @PersistenceContext
    private EntityManager em;

    @Inject
    private Principal principal;

    /**
     * Returns the current user entity.
     *
     * @return the entity with the information of the current user.
     */
    public User getCurrentUser() {
        return this.em.find(User.class, this.principal.getName());
    }
}
