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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pablog.petstore.domain.entities.Owner;
import pablog.petstore.domain.entities.OwnersDataset;
import pablog.petstore.domain.entities.Pet;
import pablog.petstore.service.util.security.RoleCaller;
import pablog.petstore.service.util.security.TestPrincipal;
import pablog.petstore.tests.dbunit.DBUnitHelper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pablog.petstore.domain.entities.OwnersDataset.*;

@ArquillianTest
class PetServiceIllegalAccessIntegrationTest {
	@Inject
	private PetService facade;

	@Inject
	private TestPrincipal principal;

	@EJB(beanName = "admin-caller")
	private RoleCaller asAdmin;

	@EJB(beanName = "owner-caller")
	private RoleCaller asOwner;

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
	void testGetNoRole() {
		assertThrows(EJBAccessException.class, () -> facade.get(existentPetId()));
	}

	@Test
	void testListNoRole() {
		assertThrows(EJBAccessException.class, () -> facade.list());
	}

	@Test
	void testCreateNoRole() {
		assertThrows(EJBAccessException.class, () -> facade.create(newPet()));
	}

	@Test
	void testUpdateNoRole() {
		assertThrows(EJBAccessException.class, () -> facade.update(anyPet()));
	}

	@Test
	void testRemoveNoRole() {
		assertThrows(EJBAccessException.class, () -> facade.remove(existentPetId()));
	}

	@Test
	void testGetRoleAdmin() {
		assertThrows(EJBAccessException.class, () -> asAdmin.run(() -> facade.get(existentPetId())));
	}

	@Test
	void testListRoleAdmin() {
		assertThrows(EJBAccessException.class, () -> asAdmin.run(() -> facade.list()));
	}

	@Test
	void testCreateRoleAdmin() {
		assertThrows(EJBAccessException.class, () -> asAdmin.run(() -> facade.create(newPet())));
	}

	@Test
	void testUpdateRoleAdmin() {
		assertThrows(EJBAccessException.class, () -> asAdmin.run(() -> facade.update(anyPet())));
	}

	@Test
	void testRemoveRoleAdmin() {
		assertThrows(EJBAccessException.class, () -> asAdmin.run(() -> facade.remove(existentPetId())));
	}

	@Test
	void testGetRoleOwner() {
		final Owner[] owners = owners();
		final Owner owner1 = owners[0];
		final Owner owner2 = owners[1];
		final Pet pet1 = owner1.getPets().iterator().next();

		principal.setName(owner2.getLogin());

		var exception = assertThrows(
			EJBTransactionRolledbackException.class,
			() -> asOwner.run(() -> facade.get(pet1.getId()))
		);
		assertInstanceOf(EJBAccessException.class, exception.getCause());
	}

	@Test
	void testCreateRoleOwner() {
		final Owner[] owners = owners();
		final Owner owner1 = owners[0];
		final Owner owner2 = owners[1];
		final Pet pet = newPetWithOwner(owner1);

		principal.setName(owner2.getLogin());

		var exception = assertThrows(
			EJBTransactionRolledbackException.class,
			() -> asOwner.run(() -> facade.create(pet))
		);
		assertInstanceOf(EJBAccessException.class, exception.getCause());
	}

	@Test
	void testUpdateRoleOwner() {
		final Owner[] owners = owners();
		final Owner owner1 = owners[0];
		final Owner owner2 = owners[1];
		final Pet pet1 = owner1.getPets().iterator().next();
		pet1.setName("Owner2 Pet");

		principal.setName(owner2.getLogin());

		var exception = assertThrows(
			EJBTransactionRolledbackException.class,
			() -> asOwner.run(() -> facade.update(pet1))
		);
		assertInstanceOf(EJBAccessException.class, exception.getCause());
	}

	@Test
	void testRemoveRoleOwner() {
		final Owner[] owners = owners();
		final Owner owner1 = owners[0];
		final Owner owner2 = owners[1];
		final Pet pet1 = owner1.getPets().iterator().next();

		principal.setName(owner2.getLogin());

		var exception = assertThrows(
			EJBTransactionRolledbackException.class,
			() -> asOwner.run(() -> facade.remove(pet1.getId()))
		);
		assertInstanceOf(EJBAccessException.class, exception.getCause());
	}
}
