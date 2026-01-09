package pablog.petstore.service;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.impl.gradle.Gradle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pablog.petstore.domain.entities.Owner;
import pablog.petstore.domain.entities.OwnersDataset;
import pablog.petstore.domain.entities.Pet;
import pablog.petstore.service.util.security.RoleCaller;
import pablog.petstore.service.util.security.TestPrincipal;
import pablog.petstore.tests.dbunit.DBUnitHelper;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pablog.petstore.domain.entities.IsEqualToOwner.containsOwnersInAnyOrder;
import static pablog.petstore.domain.entities.IsEqualToOwner.equalToOwner;
import static pablog.petstore.domain.entities.IsEqualToPet.containsPetsInAnyOrder;
import static pablog.petstore.domain.entities.OwnersDataset.*;

@ExtendWith(ArquillianExtension.class)
public class OwnerServiceIntegrationTest {

    @Inject
    private OwnerService facade;

    @EJB(beanName = "admin-caller")
    private RoleCaller asAdmin;

    @Inject
    private DBUnitHelper dbUnit;

    @Deployment
    public static Archive<?> createDeployment() {
        Archive<?>[] archives = Gradle.resolver()
                .forProjectDirectory(".")
                .importRuntimeAndTestDependencies()
                .resolve()
                .asList(JavaArchive.class).toArray(new Archive[0]);

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(OwnerService.class, OwnersDataset.class)
                .addPackage(RoleCaller.class.getPackage())
                .addPackage(Owner.class.getPackage())
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
    void testGetOwner() throws Exception {
        final Owner existentOwner = existentOwner();
        final Owner actualOwner = asAdmin.call(() -> facade.get(existentOwner.getLogin()));

        assertThat(actualOwner, is(equalToOwner(existentOwner)));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testGetOwnerNonExistent() throws Exception {
        final Owner actualOwner = asAdmin.call(() -> facade.get(nonExistentLogin()));

        assertThat(actualOwner, is(nullValue()));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testGetOwnerNull() throws Exception {
        var exception = assertThrows(
                EJBTransactionRolledbackException.class,
                () -> asAdmin.call(() -> facade.get(null))
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testList() throws Exception {
        final List<Owner> actualOwners = asAdmin.call(() -> facade.list());

        assertThat(actualOwners, containsOwnersInAnyOrder(owners()));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testFindByPetName() throws Exception {
        final String petName = petNameWithSingleOwner();
        final Owner owner = ownersOf(petName)[0];
        final List<Owner> actualOwners = asAdmin.call(() -> facade.findByPetName(petName));

        assertThat(actualOwners, hasSize(1));
        assertThat(actualOwners.getFirst(), is(equalToOwner(owner)));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testFindByPetNameMultipleOwners() throws Exception {
        final String petName = petNameWithMultipleOwners();
        final List<Owner> actualOwners = asAdmin.call(() -> facade.findByPetName(petName));
        final Owner[] expectedOwners = ownersOf(petName);

        assertThat(actualOwners, containsOwnersInAnyOrder(expectedOwners));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testFindByPetNameNoPet() throws Exception {
        final String nonExistentPet = nonExistentPetName();
        final List<Owner> actualOwners = asAdmin.call(() -> facade.findByPetName(nonExistentPet));

        assertThat(actualOwners, is(empty()));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testFindByPetNameNull() throws Exception {
        var exception = assertThrows(
                EJBTransactionRolledbackException.class,
                () -> asAdmin.run(() -> facade.findByPetName(null))
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testCreateWithoutPets() throws Exception {
        final Owner newOwner = newOwnerWithoutPets();
        final Owner actual = asAdmin.call(() -> facade.create(newOwner));

        assertThat(actual, is(equalToOwner(newOwner)));
        dbUnit.assertDataSet("owners.xml", "owners-create-without-pets.xml");
    }

//    @Test
//    void testCreateWithPets() throws Exception {
//        final Owner actualOwner = asAdmin.call(() -> facade.create(newOwnerWithFreshPets()));
//
//        assertThat(actualOwner, is(equalToOwner(newOwnerWithPersistentPets())));
//        dbUnit.assertDataSet("owners.xml", "owners-create-with-pets.xml");
//    }

    @Test
    void testCreateExistentLogin() throws Exception {
        var exception = assertThrows(
                EJBTransactionRolledbackException.class,
                () -> asAdmin.run(() -> facade.create(existentOwner()))
        );
        assertInstanceOf(EntityExistsException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testCreateNull() throws Exception {
        var exception = assertThrows(
                EJBTransactionRolledbackException.class,
                () -> asAdmin.call(() -> facade.create(null))
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testUpdateNull() throws Exception {
        var exception = assertThrows(
                EJBTransactionRolledbackException.class,
                () -> asAdmin.run(() -> facade.update(null))
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testUpdatePassword() throws Exception {
        final Owner existentOwner = existentOwner();
        existentOwner.changePassword(newPasswordForExistentOwner());
        asAdmin.run(() -> facade.update(existentOwner));

        dbUnit.assertDataSet("owners-update-password.xml");
    }

    @Test
    void testUpdateNewOwnerWithoutPets() throws Exception {
        final Owner newOwner = newOwnerWithoutPets();
        final Owner actualOwner = asAdmin.call(() -> facade.update(newOwner));

        assertThat(actualOwner, is(equalToOwner(newOwner)));
        dbUnit.assertDataSet("owners.xml", "owners-create-without-pets.xml");
    }

//    @Test
//    void testUpdateNewOwnerWithPets() throws Exception {
//        final Owner actualOwner = asAdmin.call(() -> facade.update(newOwnerWithFreshPets()));
//
//        assertThat(actualOwner, is(equalToOwner(newOwnerWithPersistentPets())));
//        dbUnit.assertDataSet("owners.xml", "owners-create-with-pets.xml");
//    }

    @Test
    void testRemoveWithoutPets() throws Exception {
        asAdmin.run(() -> facade.remove(ownerWithoutPets().getLogin()));

        dbUnit.assertDataSet("owners-remove-without-pets.xml");
    }

    @Test
    void testRemoveWithPets() throws Exception {
        asAdmin.run(() -> facade.remove(ownerWithPets().getLogin()));

        dbUnit.assertDataSet("owners-remove-with-pets.xml");
    }

    @Test
    void testRemoveNonExistentOwner() throws Exception {
        var exception = assertThrows(
                EJBTransactionRolledbackException.class,
                () -> asAdmin.run(() -> facade.remove(nonExistentLogin()))
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testRemoveNull() throws Exception {
        var exception = assertThrows(
                EJBTransactionRolledbackException.class,
                () -> asAdmin.run(() -> facade.remove(null))
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testGetPets() throws Exception {
        final Owner owner = ownerWithPets();
        final Collection<Pet> ownedPets = owner.getPets();
        final List<Pet> actualPets = asAdmin.call(() -> facade.getPets(owner.getLogin()));

        assertThat(actualPets, containsPetsInAnyOrder(ownedPets));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testGetPetsNoPets() throws Exception {
        final List<Pet> actualPets = asAdmin.call(() -> facade.getPets(ownerWithoutPets().getLogin()));

        assertThat(actualPets, is(empty()));
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testGetPetsNonExistentOwner() throws Exception {
        var exception = assertThrows(
                EJBTransactionRolledbackException.class,
                () -> asAdmin.call(() -> facade.getPets(nonExistentLogin()))
        );
        assertInstanceOf(NullPointerException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }

    @Test
    void testGetPetsNull() throws Exception {
        var exception = assertThrows(
                EJBTransactionRolledbackException.class,
                () -> asAdmin.call(() -> facade.getPets(null))
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        dbUnit.assertDataSet("owners.xml");
    }
}
