package pablog.petstore.service;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.inject.Inject;
import org.dbunit.DatabaseUnitException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.impl.gradle.Gradle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pablog.petstore.domain.entities.AnimalType;
import pablog.petstore.domain.entities.Owner;
import pablog.petstore.domain.entities.OwnersDataset;
import pablog.petstore.domain.entities.Pet;
import pablog.petstore.service.util.security.RoleCaller;
import pablog.petstore.service.util.security.TestPrincipal;
import pablog.petstore.tests.dbunit.DBUnitHelper;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pablog.petstore.domain.entities.IsEqualToPet.containsPetsInAnyOrder;
import static pablog.petstore.domain.entities.IsEqualToPet.equalToPet;
import static pablog.petstore.domain.entities.OwnersDataset.anyPetOf;
import static pablog.petstore.domain.entities.OwnersDataset.existentPet;
import static pablog.petstore.domain.entities.OwnersDataset.existentPetId;
import static pablog.petstore.domain.entities.OwnersDataset.newPet;
import static pablog.petstore.domain.entities.OwnersDataset.newPetWithOwner;
import static pablog.petstore.domain.entities.OwnersDataset.nonExistentPetId;
import static pablog.petstore.domain.entities.OwnersDataset.ownerWithPets;
import static pablog.petstore.domain.entities.OwnersDataset.ownerWithoutPets;
import static pablog.petstore.domain.entities.OwnersDataset.petWithId;

@ArquillianTest
class PetServiceIntegrationTest {

    @Inject
    private PetService facade;

    @EJB(beanName = "owner-caller")
    private RoleCaller asOwner;

    @Inject
    private TestPrincipal principal;

    @Inject
    private DBUnitHelper dbUnit;

    @Deployment
    static Archive<?> createDeployment() {
        Archive<?>[] archives = Gradle.resolver()
                .forProjectDirectory(".")
                .importRuntimeAndTestDependencies()
                .resolve()
                .asList(JavaArchive.class).toArray(new Archive[0]);

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(PetService.class, OwnersDataset.class)
                .addPackage(RoleCaller.class.getPackage())
                .addPackage(Pet.class.getPackage())
                .addPackage(DBUnitHelper.class.getPackage())
                .addPackage(TestPrincipal.class.getPackage())
                .addAsLibraries(archives)
                .addAsResource(new File("../tests/src/main/resources/"), "")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("beans.xml", "beans.xml");
    }

    @BeforeEach
    void setUp() throws Exception {
        dbUnit.loadDataSet("owners.xml");
    }

    @AfterEach
    void tearDown() throws Exception {
        dbUnit.executeCleanupScripts("scripts/cleanup.sql", "scripts/cleanup-autoincrement.sql");
    }

    @Test
    void testGet() throws Exception {
        final Pet existentPet = existentPet();

        principal.setName(existentPet.getOwner().getLogin());

        final Pet actualPet = asOwner.call(() -> facade.get(existentPet.getId()));

        assertThat(actualPet, equalToPet(existentPet));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testGetBadId() throws Exception {
        principal.setName(ownerWithoutPets().getLogin());

        final Pet actual = asOwner.call(() -> facade.get(nonExistentPetId()));

        assertThat(actual, is(nullValue()));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testGetOthersPetId() throws Exception {
        final Owner ownerWithoutPets = ownerWithoutPets();
        final int petId = anyPetOf(ownerWithPets()).getId();

        principal.setName(ownerWithoutPets.getLogin());

        assertThrows(EJBTransactionRolledbackException.class, () -> asOwner.run(() -> facade.get(petId)));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testList() throws Exception {
        final Owner ownerWithPets = ownerWithPets();

        principal.setName(ownerWithPets.getLogin());

        final List<Pet> actualPets = asOwner.call(() -> facade.list());

        assertThat(actualPets, containsPetsInAnyOrder(ownerWithPets.getPets()));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testListNoPets() throws Exception {
        principal.setName(ownerWithoutPets().getLogin());

        final List<Pet> pets = asOwner.call(() -> facade.list());

        assertThat(pets, is(empty()));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testCreate() throws Exception {
        final Owner ownerWithoutPets = ownerWithoutPets();

        principal.setName(ownerWithoutPets.getLogin());

        final Pet pet = newPetWithOwner(ownerWithoutPets);

        asOwner.call(() -> facade.create(pet));
        dbUnit.assertDataSet("owners.xml", "owners-create-pet.xml");
    }

    @Test
    void testCreateNullOwner() throws Exception {
        principal.setName(ownerWithoutPets().getLogin());

        final Pet pet = newPet();

        asOwner.run(() -> facade.create(pet));
        dbUnit.assertDataSet("owners.xml", "owners-create-pet.xml");
    }

    @Test
    void testCreateNullPet() throws Exception {
        principal.setName(ownerWithoutPets().getLogin());

        assertThrows(EJBTransactionRolledbackException.class, () -> asOwner.run(() -> facade.create(null)));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testCreateWrongOwner() throws Exception {
        principal.setName(ownerWithoutPets().getLogin());

        final Pet pet = newPetWithOwner(ownerWithPets());

        assertThrows(EJBTransactionRolledbackException.class, () -> asOwner.run(() -> facade.create(pet)));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testUpdate() throws Exception {
        final Pet existentPet = existentPet();

        principal.setName(existentPet.getOwner().getLogin());

        existentPet.setName("UpdateName");
        existentPet.setAnimal(AnimalType.BIRD);
        existentPet.setBirth(new Date(946771261000L));

        asOwner.run(() -> facade.update(existentPet));
        dbUnit.assertDataSet("owners-update-pet.xml");
    }

    @Test
    void testUpdateNewPetWithOwner() throws Exception {
        final Owner ownerWithoutPets = ownerWithoutPets();

        principal.setName(ownerWithoutPets.getLogin());

        final Pet pet = newPetWithOwner(ownerWithoutPets);

        asOwner.call(() -> facade.update(pet));
        dbUnit.assertDataSet("owners.xml", "owners-create-pet.xml");
    }

    @Test
    void testUpdateNull() throws Exception {
        assertThrows(EJBTransactionRolledbackException.class, () -> asOwner.run(() -> facade.update(null)));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testUpdateWrongOwner() throws Exception {
        principal.setName(ownerWithoutPets().getLogin());

        final Pet pet = anyPetOf(ownerWithPets());

        var exception = assertThrows(
            EJBTransactionRolledbackException.class, 
            () -> asOwner.run(() -> facade.update(pet))
        );
        assertInstanceOf(EJBAccessException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testUpdatePetNoOwner() throws Exception {
        final int id = existentPetId();
        final Pet pet = petWithId(id);

        principal.setName(pet.getOwner().getLogin());
        pet.setOwner(null);

        assertThrows(EJBTransactionRolledbackException.class, () -> asOwner.run(() -> facade.update(pet)));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testRemove() throws Exception {
        final Pet existentPet = existentPet();

        principal.setName(existentPet.getOwner().getLogin());

        asOwner.run(() -> facade.remove(existentPet.getId()));
        dbUnit.assertDataSet("owners-remove-pet.xml");
    }

    @Test
    void testRemoveBadId() throws Exception {
        principal.setName(ownerWithoutPets().getLogin());

        assertThrows(EJBTransactionRolledbackException.class, () -> asOwner.run(() -> facade.remove(nonExistentPetId())));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testRemoveOthersPetId() throws Exception {
        final Owner ownerWithoutPets = ownerWithoutPets();
        final int petId = anyPetOf(ownerWithPets()).getId();

        principal.setName(ownerWithoutPets.getLogin());

        assertThrows(EJBTransactionRolledbackException.class, () -> asOwner.run(() -> facade.remove(petId)));
        dbUnit.assertDataSet("owners.xml");
    }
}
