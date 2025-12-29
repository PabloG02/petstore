package pablog.petstore.rest;

import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import pablog.petstore.domain.entities.Owner;
import pablog.petstore.rest.GenericTypes.ListOwnerType;
import pablog.petstore.rest.entity.OwnerCreationData;
import pablog.petstore.rest.entity.OwnerEditionData;
import pablog.petstore.service.OwnerService;
import pablog.petstore.service.util.security.RoleCaller;
import pablog.petstore.service.util.security.TestPrincipal;
import pablog.petstore.tests.dbunit.DBUnitHelper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.impl.gradle.Gradle;
import org.junit.jupiter.api.*;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.net.URL;
import java.util.List;

import static pablog.petstore.domain.entities.IsEqualToOwner.containsOwnersInAnyOrder;
import static pablog.petstore.domain.entities.IsEqualToOwner.equalToOwner;
import static pablog.petstore.domain.entities.OwnersDataset.*;
import static pablog.petstore.http.util.HasHttpStatus.*;
import static jakarta.ws.rs.client.Entity.json;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ArquillianTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OwnerResourceRestTest {
    private static final String BASE_PATH = "api/owners/";
    private static final String BASIC_AUTHORIZATION = "Basic am9zZTpqb3NlcGFzcw==";
    @ArquillianResource
    private URL deploymentUrl;
    @Inject
    private DBUnitHelper dbUnit;
    private Client client;

    @Deployment
    static Archive<?> createDeployment() {
        Archive<?>[] archives = Gradle.resolver()
                .forProjectDirectory(".")
                .importRuntimeAndTestDependencies()
                .resolve()
                .asList(JavaArchive.class).toArray(new Archive[0]);

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(OwnerResource.class.getPackage())
                .addPackage(OwnerCreationData.class.getPackage())
                .addPackage(OwnerService.class.getPackage())
                .addPackage(Owner.class.getPackage())
                .addPackage(DBUnitHelper.class.getPackage())
                .addPackage(TestPrincipal.class.getPackage())
                .addPackage(RoleCaller.class.getPackage())
                .addAsLibraries(archives)
                .addAsResource(new File("../tests/src/main/resources/"), "")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                //.addAsWebInfResource("jboss-web.xml")
                .addAsWebInfResource("web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

    }

    @Test
    @Order(1)
    void beforeGet() throws Exception {
        dbUnit.loadDataSet("owners.xml");
    }

    @Test
    @Order(2)
    @RunAsClient
    void testGet() {
        client = ClientBuilder.newClient();
        final Response response = authorizedJsonRequestGet(deploymentUrl + BASE_PATH + EXISTENT_LOGIN);

        assertThat(response, hasOkStatus());

        final Owner owner = response.readEntity(Owner.class);
        final Owner expected = existentOwner();

        assertThat(owner, is(equalToOwner(expected)));
    }

    @Test
    @Order(3)
    void afterGet() throws Exception {
        dbUnit.assertDataSet("owners.xml");
        dbUnit.executeCleanupScripts("scripts/cleanup.sql", "scripts/cleanup-autoincrement.sql");
    }

    @Test
    @Order(4)
    void beforeGetNonExistent() throws Exception {
        dbUnit.loadDataSet("owners.xml");
    }

    @Test
    @Order(5)
    @RunAsClient
    void testGetNonExistent() {
        client = ClientBuilder.newClient();
        final Response response = authorizedJsonRequestGet(deploymentUrl + BASE_PATH + NON_EXISTENT_LOGIN);

        assertThat(response, hasBadRequestStatus());
    }

    @Test
    @Order(6)
    void afterGetNonExistent() throws Exception {
        dbUnit.assertDataSet("owners.xml");
        dbUnit.executeCleanupScripts("scripts/cleanup.sql", "scripts/cleanup-autoincrement.sql");
    }

    @Test
    @Order(10)
    void beforeList() throws Exception {
        dbUnit.loadDataSet("owners.xml");
    }

    @Test
    @Order(11)
    @RunAsClient
    void testList() {
        client = ClientBuilder.newClient();
        final Response response = authorizedJsonRequestGet(deploymentUrl + BASE_PATH);

        assertThat(response, hasOkStatus());

        final List<Owner> list = ListOwnerType.readEntity(response);
        assertThat(list, containsOwnersInAnyOrder(owners()));
    }

    @Test
    @Order(12)
    void afterList() throws Exception {
        dbUnit.assertDataSet("owners.xml");
        dbUnit.executeCleanupScripts("scripts/cleanup.sql", "scripts/cleanup-autoincrement.sql");
    }

    @Test
    @Order(20)
    void beforeCreate() throws Exception {
        dbUnit.loadDataSet("owners.xml");
    }

    @Test
    @Order(21)
    @RunAsClient
    void testCreate() {
        client = ClientBuilder.newClient();
        final Owner newOwner = newOwnerWithoutPets();
        final OwnerCreationData ownerData = new OwnerCreationData(newOwnerLogin(), newOwnerPassword());

        final Response response = client.target(deploymentUrl + BASE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", BASIC_AUTHORIZATION)
                .post(json(ownerData));

        assertThat(response, hasCreatedStatus());

        final String location = response.getHeaderString("Location");

        final Response responseGet = authorizedJsonRequestGet(location);
        final Owner owner = responseGet.readEntity(Owner.class);
        assertThat(owner, is(equalToOwner(newOwner)));
    }

    @Test
    @Order(22)
    void afterCreate() throws Exception {
        dbUnit.assertDataSet("owners.xml", "owners-create-without-pets.xml");
        dbUnit.executeCleanupScripts("scripts/cleanup.sql", "scripts/cleanup-autoincrement.sql");
    }

    @Test
    @Order(30)
    public void beforeUpdatePassword() throws Exception {
        dbUnit.loadDataSet("owners.xml");
    }

    @Test
    @Order(31)
    @RunAsClient
    void testUpdatePassword() {
        client = ClientBuilder.newClient();
        final OwnerEditionData ownerData = new OwnerEditionData(newPasswordForExistentOwner());

        final Response response = client.target(deploymentUrl + BASE_PATH + EXISTENT_LOGIN)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", BASIC_AUTHORIZATION)
                .put(json(ownerData));

        assertThat(response, hasOkStatus());
    }

    @Test
    @Order(32)
    void afterUpdatePassword() throws Exception {
        dbUnit.assertDataSet("owners-update-password.xml");
        dbUnit.executeCleanupScripts("scripts/cleanup.sql", "scripts/cleanup-autoincrement.sql");
    }

    @Test
    @Order(40)
    public void beforeDeleteWithoutPets() throws Exception {
        dbUnit.loadDataSet("owners.xml");
    }

    @Test
    @Order(41)
    @RunAsClient
    void testDeleteWithoutPets() {
        client = ClientBuilder.newClient();
        final Response response = client.target(deploymentUrl + BASE_PATH + OWNER_WITHOUT_PETS_LOGIN)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", BASIC_AUTHORIZATION)
                .delete();

        assertThat(response, hasOkStatus());
    }

    @Test
    @Order(42)
    void afterDeleteWithoutPets() throws Exception {
        dbUnit.assertDataSet("owners-remove-without-pets.xml");
        dbUnit.executeCleanupScripts("scripts/cleanup.sql", "scripts/cleanup-autoincrement.sql");
    }

    @Test
    @Order(43)
    void beforeDeleteWithPets() throws Exception {
        dbUnit.loadDataSet("owners.xml");
    }

    @Test
    @Order(44)
    @RunAsClient
    void testDeleteWithPets() {
        client = ClientBuilder.newClient();
        final Response response = client.target(deploymentUrl + BASE_PATH + OWNER_WITH_PETS_LOGIN)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", BASIC_AUTHORIZATION)
                .delete();

        assertThat(response, hasOkStatus());
    }

    @Test
    @Order(45)
    void afterDeleteWithPets() throws Exception {
        dbUnit.assertDataSet("owners-remove-with-pets.xml");
        dbUnit.executeCleanupScripts("scripts/cleanup.sql", "scripts/cleanup-autoincrement.sql");
    }

    @Test
    @Order(46)
    void beforeDeleteNoLogin() throws Exception {
        dbUnit.loadDataSet("owners.xml");
    }

    @Test
    @Order(47)
    @RunAsClient
    void testDeleteNoLogin() {
        client = ClientBuilder.newClient();
        final Response response = client.target(deploymentUrl + BASE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", BASIC_AUTHORIZATION)
                .delete();

        assertThat(response, hasMethodNotAllowedStatus());
    }

    @Test
    @Order(48)
    void afterDeleteNoLogin() throws Exception {
        dbUnit.assertDataSet("owners.xml");
        dbUnit.executeCleanupScripts("scripts/cleanup.sql", "scripts/cleanup-autoincrement.sql");
    }

    private Response authorizedJsonRequestGet(String uri) {
        System.out.println("GET -> " + uri);
        return client.target(uri)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", BASIC_AUTHORIZATION)
                .get();
    }
}
