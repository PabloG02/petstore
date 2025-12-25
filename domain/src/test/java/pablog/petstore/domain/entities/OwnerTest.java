package pablog.petstore.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static java.util.Arrays.copyOfRange;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OwnerTest extends UserTest {
	private Pet[] pets;
	
	private Pet petOwned;
	private Pet petNotOwned;
	private Pet[] petsWithoutOwned;

	@BeforeEach
	public void setUpOwner() {
		this.pets = new Pet[] {
			new Pet("Lassie", AnimalType.DOG, new Date()),
			new Pet("Pajaroto", AnimalType.BIRD, new Date())
		};
		
		this.petNotOwned = new Pet("Doraemon", AnimalType.CAT, new Date());
		this.petOwned = this.pets[1];
		this.petsWithoutOwned = copyOfRange(this.pets, 0, 1);
	}
	
	@Override
	protected User newUser(String login, String password) {
		return new Owner(login, password);
	}

	@Test
	public void testOwnerStringStringCollection() {
		final String[] logins = { login, "A", repeat('A', 100) };
		
		for (String login : logins) {
			final Owner owner = new Owner(login, password, pets);
			
			assertThat(owner.getLogin(), is(equalTo(login)));
			assertThat(owner.getPassword(), is(equalTo(md5Pass)));
			assertThat(owner.getPets(), containsInAnyOrder(pets));
		}
	}
	
	@Test
	public void testOwnerStringStringCollectionEmptyPets() {
		final Owner owner = new Owner(login, password, new Pet[0]);
		
		assertThat(owner.getLogin(), is(equalTo(login)));
		assertThat(owner.getPassword(), is(equalTo(md5Pass)));
		assertThat(owner.getPets(), is(emptyIterable()));
	}

	@Test
	public void testOwnerStringStringCollectionNullLogin() {
		assertThrows(NullPointerException.class, () -> new Owner(null, password, pets));
	}
	
	@Test
	public void testOwnerStringStringCollectionNullPassword() {
		assertThrows(NullPointerException.class, () -> new Owner(login, null, pets));
	}
	
	@Test
	public void testOwnerStringStringCollectionLoginTooShort() {
		assertThrows(IllegalArgumentException.class, () -> new Owner(shortLogin, password, pets));
	}
	
	@Test
	public void testOwnerStringStringCollectionLoginTooLong() {
		assertThrows(IllegalArgumentException.class, () -> new Owner(longLogin, password, pets));
	}
	
	@Test
	public void testOwnerStringStringCollectionPasswordTooShort() {
		assertThrows(IllegalArgumentException.class, () -> new Owner(login, shortPassword, pets));
	}
	
	@Test
	public void testOwnerStringStringCollectionNullPets() {
		assertThrows(NullPointerException.class, () -> new Owner(login, password, (Pet[]) null));
	}
	
	@Test
	public void testOwnerStringStringCollectionPasswordPetsWithNull() {
		assertThrows(NullPointerException.class, () -> new Owner(login, password, new Pet[] { petNotOwned, null }));
	}

	@Test
	public void testAddPet() {
		final Owner owner = new Owner(login, password);
		
		owner.addPet(petNotOwned);
		
		assertThat(owner.getPets(), contains(petNotOwned));
		assertThat(petNotOwned.getOwner(), is(owner));
	}

	@Test
	public void testAddPetAlreadyOwned() {
		final Owner owner = new Owner(login, password, pets);
		
		owner.addPet(petOwned);
		
		assertThat(owner.getPets(), containsInAnyOrder(pets));
	}

	@Test
	public void testAddPetNull() {
		final Owner owner = new Owner(login, password);
		
		assertThrows(NullPointerException.class, () -> owner.addPet(null));
	}

	@Test
	public void testRemovePet() {
		final Owner owner = new Owner(login, password, pets);
		
		owner.removePet(petOwned);
		assertThat(owner.getPets(), contains(petsWithoutOwned));
		assertThat(petOwned.getOwner(), is(nullValue()));
	}

	@Test
	public void testRemovePetNull() {
		final Owner owner = new Owner(login, password);
		
		assertThrows(NullPointerException.class, () -> owner.removePet(null));
	}

	@Test
	public void testRemovePetNotOwned() {
		final Owner owner = new Owner(login, password);
		
		assertThrows(IllegalArgumentException.class, () -> owner.removePet(petNotOwned));
	}

	@Test
	public void testOwnsPet() {
		final Owner owner = new Owner(login, password, pets);

		for (Pet pet : pets) {
			assertThat(owner.ownsPet(pet), is(true));
		}
		assertThat(owner.ownsPet(petNotOwned), is(false));
	}

	@Test
	public void testOwnsPetNotOwned() {
		final Owner owner = new Owner(login, password, pets);

		assertThat(owner.ownsPet(petNotOwned), is(false));
	}

	@Test
	public void testOwnsPetNull() {
		final Owner owner = new Owner(login, password, pets);

		assertThat(owner.ownsPet(null), is(false));
	}

	@Test
	public void testInternalAddPet() {
		final Owner owner = new Owner(login, password);
		
		owner.internalAddPet(petNotOwned);
		
		assertThat(owner.getPets(), contains(petNotOwned));
	}

	@Test
	public void testInternalAddPetAlreadyOwned() {
		final Owner owner = new Owner(login, password, pets);
		
		owner.internalAddPet(petOwned);
		
		assertThat(owner.getPets(), containsInAnyOrder(pets));
	}

	@Test
	public void testInternalAddPetNull() {
		final Owner owner = new Owner(login, password);
		
		assertThrows(NullPointerException.class, () -> owner.internalAddPet(null));
	}

	@Test
	public void testInternalRemovePet() {
		final Owner owner = new Owner(login, password, pets);
		
		owner.internalRemovePet(petOwned);
		assertThat(owner.getPets(), contains(petsWithoutOwned));
	}

	@Test
	public void testSetLoginNullValue() {
		final Owner owner = new Owner(login, password);
		
		assertThrows(NullPointerException.class, () -> owner.setLogin(null));
	}
	
	@Test
	public void testInternalRemovePetNull() {
		final Owner owner = new Owner(login, password, pets);
		
		assertThrows(NullPointerException.class, () -> owner.internalRemovePet(null));
	}
}
