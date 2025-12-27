package pablog.petstore.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pablog.petstore.domain.entities.Owner;
import pablog.petstore.domain.entities.Pet;

import java.util.ArrayList;
import java.util.List;

/**
 * EJB for the Owners. Only administrators have access to this class.
 *
 * @author Miguel Reboiro Jato
 */
@Stateless
@RolesAllowed("ADMIN")
public class OwnerService {
    @PersistenceContext
    private EntityManager em;

    /**
     * Returns the owner identified by {@code login}. If there is no owner with
     * the specified login, {@code null} will be returned.
     *
     * @param login the login of an owner.
     * @return the owner with the provided login or {@code null} if there is no
     * owner with the specified login.
     * @throws IllegalArgumentException if {@code login} is {@code null} or it
     *                                  does not identify a valid owner.
     */
    public Owner get(String login) {
        return em.find(Owner.class, login);
    }

    /**
     * Returns the complete list of owners.
     *
     * @return the complete list of owners.
     */
    public List<Owner> list() {
        return em.createQuery("SELECT o FROM Owner o", Owner.class)
                .getResultList();
    }

    /**
     * Returns the list of owners that have a pet with the specified name.
     *
     * @param petName a pet's name.
     * @return the list of owners that have a pet with the specified name. The
     * list may be empty if any owner has a pet with the specified name.
     * @throws IllegalArgumentException if {@code petName} is {@code null}.
     */
    public List<Owner> findByPetName(String petName) {
        if (petName == null)
            throw new IllegalArgumentException("petName can't be null");

        final String query = "SELECT o FROM Owner o JOIN o.pets p " +
                "WHERE p.name = :petName";

        return em.createQuery(query, Owner.class)
                .setParameter("petName", petName)
                .getResultList();
    }

    /**
     * Creates a new owner. If the owner already has pets, they will be created
     * too.
     *
     * @param owner a new owner to be stored.
     * @return the persistent version of the owner created.
     * @throws IllegalArgumentException if {@code owner} is {@code null}.
     * @throws EntityExistsException    if an owner with the same login already
     *                                  exists.
     */
    public Owner create(Owner owner) {
        if (owner == null)
            throw new IllegalArgumentException("owner can't be null");

        this.em.persist(owner);

        return owner;
    }

    /**
     * Updates a new owner. If the owner is not stored, it will be persisted.
     *
     * @param owner an owner to be updated.
     * @return the updated owner.
     * @throws IllegalArgumentException if {@code owner} is {@code null}.
     */
    public Owner update(Owner owner) {
        if (owner == null)
            throw new IllegalArgumentException("owner can't be null");

        return em.merge(owner);
    }

    /**
     * Deletes an owner.
     *
     * @param login the login of the owner to be deleted.
     * @throws IllegalArgumentException if {@code login} is {@code null} or if
     *                                  it does not identify a valid owner.
     */
    public void remove(String login) {
        em.remove(this.get(login));
    }

    /**
     * Returns the list of pets of an owner.
     *
     * @param login the login of the owner that owns the pets.
     * @return the list of pets of an owner.
     * @throws IllegalArgumentException if {@code login} is {@code null} or it
     *                                  does not identify a valid owner.
     */
    public List<Pet> getPets(String login) {
        return new ArrayList<>(this.get(login).getPets());
    }
}
