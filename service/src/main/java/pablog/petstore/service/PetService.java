package pablog.petstore.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pablog.petstore.domain.entities.Owner;
import pablog.petstore.domain.entities.Pet;

import java.security.Principal;
import java.util.List;

/**
 * EJB for the Pets. Only owners have access to this class, and only to their
 * own pets.
 *
 * @author Miguel Reboiro Jato
 */
@Stateless
@RolesAllowed("OWNER")
public class PetService {
    @Inject
    private Principal currentOwner;

    @PersistenceContext
    private EntityManager em;

    /**
     * Returns a pet identified by the provided id. If an owner tries to access
     * a pet that does now own, an {@link EJBAccessException} will be thrown.
     *
     * @param id the identified of a pet.
     * @return a pet identified by the provided identifier or {@code null} if no
     * pet exists with the provided identifier.
     * @throws EJBAccessException if the current owner does not own the pet.
     */
    public Pet get(int id) {
        final Pet pet = em.find(Pet.class, id);

        if (pet == null) {
            return null;
        } else if (pet.getOwner().getLogin().equals(this.currentOwner.getName())) {
            return pet;
        } else {
            throw new EJBAccessException("Pet's owner is not the current principal");
        }
    }

    /**
     * Returns the complete list of pets of the current owner.
     *
     * @return the complete list of pets of the current owner.
     */
    public List<Pet> list() {
        return em.createQuery("SELECT p FROM Pet p WHERE p.owner.login = :login", Pet.class)
                .setParameter("login", currentOwner.getName())
                .getResultList();
    }

    /**
     * Creates a new pet owned by the current user.
     *
     * @param pet a new pet to be stored.
     * @return the persistent version of the pet created.
     * @throws EJBAccessException       if the pet already has an owner, and it is not
     *                                  the current user. If the pet has no owner, this exception will never be
     *                                  thrown.
     * @throws IllegalArgumentException if a pet with the same identifier
     *                                  already exists.
     */
    public Pet create(Pet pet) {
        final Owner owner = em.find(Owner.class, currentOwner.getName());

        if (pet.getOwner() != null && !pet.getOwner().getLogin().equals(owner.getLogin())) {
            throw new EJBAccessException("Pet's owner is not the current principal");
        } else {
            pet.setOwner(owner);

            this.em.persist(pet);

            return pet;
        }
    }

    /**
     * Updates the information of a pet. If the pet is not stored, it will be
     * created.
     *
     * @param pet a pet to be updated.
     * @return the updated pet.
     * @throws IllegalArgumentException if the pet has no owner.
     * @throws EJBAccessException       if the pet's owner is not the current user.
     */
    public Pet update(Pet pet) {
        if (pet.getOwner() == null)
            throw new IllegalArgumentException("Pet must have an owner");

        if (pet.getOwner().getLogin().equals(this.currentOwner.getName())) {
            return em.merge(pet);
        } else {
            throw new EJBAccessException("Pet's owner is not the current principal");
        }
    }

    /**
     * Deletes a pet.
     *
     * @param id the identifier of the pet to be deleted.
     * @throws IllegalArgumentException if there is no pet with the provided
     *                                  identifier.
     * @throws EJBAccessException       if the pet's owner is not the current user.
     */
    public void remove(int id) {
        final Pet pet = this.get(id);
        pet.setOwner(null);

        em.remove(pet);
    }
}
