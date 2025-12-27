package pablog.petstore.service;

import jakarta.ejb.EJBAccessException;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.impl.gradle.Gradle;
import org.junit.jupiter.api.Test;
import pablog.petstore.domain.entities.User;
import pablog.petstore.service.util.security.RoleCaller;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ArquillianTest
class UserServiceIntegrationTest {

    @Inject
    private UserService facade;

    @Deployment
    static WebArchive createDeployment() {
        Archive<?>[] archives = Gradle.resolver()
                .forProjectDirectory(".")
                .importRuntimeAndTestDependencies()
                .resolve()
                .asList(JavaArchive.class).toArray(new Archive[0]);

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(RoleCaller.class.getPackage())
                .addPackage(User.class.getPackage())
                .addPackage(UserService.class.getPackage())
                .addAsLibraries(archives)
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    void testGetCredentialsNoUser() {
        assertThrows(EJBAccessException.class, () -> facade.getCurrentUser());
    }
}