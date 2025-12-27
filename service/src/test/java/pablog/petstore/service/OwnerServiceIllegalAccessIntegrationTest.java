package pablog.petstore.service;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.impl.gradle.Gradle;
import org.junit.jupiter.api.Test;
import pablog.petstore.domain.entities.Owner;
import pablog.petstore.domain.entities.OwnersDataset;
import pablog.petstore.service.util.security.RoleCaller;
import pablog.petstore.service.util.security.TestPrincipal;
import pablog.petstore.tests.dbunit.DBUnitHelper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static pablog.petstore.domain.entities.OwnersDataset.*;

@ArquillianTest
class OwnerServiceIllegalAccessIntegrationTest {

    @Inject
    private OwnerService facade;

    @EJB(beanName = "owner-caller")
    private RoleCaller asOwner;

    @Deployment
    static Archive<?> createDeployment() {
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

    @Test
    void testGetNoRole() {
        assertThrows(EJBAccessException.class, () -> facade.get(existentLogin()));
    }

    @Test
    void testListNoRole() {
        assertThrows(EJBAccessException.class, () -> facade.list());
    }

    @Test
    void testFindByPetNameNoRole() {
        assertThrows(EJBAccessException.class, () -> facade.findByPetName(existentPetName()));
    }

    @Test
    void testCreateNoRole() {
        assertThrows(EJBAccessException.class, () -> facade.create(newOwnerWithoutPets()));
    }

    @Test
    void testUpdateNoRole() {
        assertThrows(EJBAccessException.class, () -> facade.update(anyOwner()));
    }

    @Test
    void testRemoveNoRole() {
        assertThrows(EJBAccessException.class, () -> facade.remove(existentLogin()));
    }

    @Test
    void testGetPetsNoRole() {
        assertThrows(EJBAccessException.class, () -> facade.getPets(existentLogin()));
    }

    @Test
    void testGetRoleOwner() {
        assertThrows(EJBAccessException.class, () -> asOwner.run(() -> facade.get(existentLogin())));
    }

    @Test
    void testListRoleOwner() {
        assertThrows(EJBAccessException.class, () -> asOwner.run(() -> facade.list()));
    }

    @Test
    void testFindByPetNameRoleOwner() {
        assertThrows(EJBAccessException.class, () -> asOwner.run(() -> facade.findByPetName(existentPetName())));
    }

    @Test
    void testCreateRoleOwner() {
        assertThrows(EJBAccessException.class, () -> asOwner.run(() -> facade.create(newOwnerWithoutPets())));
    }

    @Test
    void testUpdateRoleOwner() {
        assertThrows(EJBAccessException.class, () -> asOwner.run(() -> facade.update(anyOwner())));
    }

    @Test
    void testRemoveRoleOwner() {
        assertThrows(EJBAccessException.class, () -> asOwner.run(() -> facade.remove(existentLogin())));
    }

    @Test
    void testGetPetsRoleOwner() {
        assertThrows(EJBAccessException.class, () -> asOwner.run(() -> facade.getPets(existentLogin())));
    }
}
