package pablog.petstore.service;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
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
import pablog.petstore.domain.entities.Owner;
import pablog.petstore.domain.entities.Pet;
import pablog.petstore.domain.entities.User;
import pablog.petstore.domain.entities.OwnersDataset;
import pablog.petstore.domain.entities.UsersDataset;
import pablog.petstore.service.util.security.RoleCaller;
import pablog.petstore.service.util.security.TestPrincipal;
import pablog.petstore.tests.dbunit.DBUnitHelper;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static pablog.petstore.domain.entities.IsEqualToUser.equalToUser;
import static pablog.petstore.domain.entities.OwnersDataset.existentOwner;
import static pablog.petstore.domain.entities.UsersDataset.existentAdmin;

@ArquillianTest
class UserServiceIllegalAccessIntegrationTest {

    @Inject
    private UserService facade;

    @EJB(beanName = "owner-caller")
    private RoleCaller asOwner;

    @EJB(beanName = "admin-caller")
    private RoleCaller asAdmin;

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
                .addClasses(UserService.class, UsersDataset.class, OwnersDataset.class)
                .addPackage(RoleCaller.class.getPackage())
                .addPackage(Pet.class.getPackage())
                .addPackage(TestPrincipal.class.getPackage())
                .addPackage(DBUnitHelper.class.getPackage())
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
    void testGetOwnerCredentials() {
        final Owner owner = existentOwner();

        principal.setName(owner.getLogin());

        final User actualUser = asOwner.call(() -> facade.getCurrentUser());

        assertThat(actualUser, is(equalToUser(owner)));
    }

    @Test
    void testGetAdminCredentials() {
        final User admin = existentAdmin();

        principal.setName(admin.getLogin());

        final User actualUser = asAdmin.call(() -> facade.getCurrentUser());

        assertThat(actualUser, is(equalToUser(admin)));
    }

}
