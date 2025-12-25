package pablog.petstore.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PetTest {
	private String name;
	private AnimalType animalType;
	private Date birth;
	private Owner owner;
	
	private String newName;
	private AnimalType newAnimalType;
	private Owner newOwner;
	private Date futureDate;
	private Date newBirth;

	@BeforeEach
	public void setUp() throws Exception {
		this.name = "Rex";
		this.animalType = AnimalType.DOG;
		this.birth = new Date();
		this.owner = new Owner("pepe", "pepepass");
		
		this.newName = "Ein";
		this.newAnimalType = AnimalType.BIRD;
		this.newOwner = new Owner("José", "josepass");
		final int oneDay = 24*60*60*1000;
		this.newBirth = new Date(System.currentTimeMillis() - oneDay);
		this.futureDate = new Date(System.currentTimeMillis() + oneDay);
	}

	@Test
	public void testPetStringAnimalTypeDate() {
		final String[] names = { name, "A", repeat("A", 100)};
		
		for (String name : names) {
			final Pet pet = new Pet(name, animalType, birth);
			
			assertThat(pet.getName(), is(equalTo(name)));
			assertThat(pet.getAnimal(), is(equalTo(animalType)));
			assertThat(pet.getBirth(), is(equalTo(birth)));
			assertThat(pet.getOwner(), is(nullValue()));
		}
	}

	@Test
	public void testPetStringAnimalTypeDateNullName() {
		assertThrows(NullPointerException.class, () -> new Pet(null, animalType, birth));
	}

	@Test
	public void testPetStringAnimalTypeDateNameTooShort() {
		assertThrows(IllegalArgumentException.class, () -> new Pet("", animalType, birth));
	}

	@Test
	public void testPetStringAnimalTypeDateNameTooLong() {
		assertThrows(IllegalArgumentException.class, () -> new Pet(repeat('A', 101), animalType, birth));
	}
	
	@Test
	public void testPetStringAnimalTypeDateNullAnimal() {
		assertThrows(NullPointerException.class, () -> new Pet(name, null, birth));
	}
	
	@Test
	public void testPetStringAnimalTypeDateNullBirth() {
		assertThrows(NullPointerException.class, () -> new Pet(name, animalType, null));
	}
	
	@Test
	public void testPetStringAnimalTypeDateBirthAfterCurrent() {
		assertThrows(IllegalArgumentException.class, () -> new Pet(name, animalType, futureDate));
	}

	@Test
	public void testPetStringAnimalTypeDateOwner() {
		final String[] names = { name, "A", repeat("A", 100)};
		
		for (String name : names) {
			final Pet pet = new Pet(name, animalType, birth, owner);
			
			assertThat(pet.getName(), is(equalTo(name)));
			assertThat(pet.getAnimal(), is(equalTo(animalType)));
			assertThat(pet.getBirth(), is(equalTo(birth)));
			assertThat(pet.getOwner(), is(equalTo(owner)));
			assertThat(owner.ownsPet(pet), is(true));
		}
	}

	@Test
	public void testPetStringAnimalTypeDateOwnerNullName() {
		assertThrows(NullPointerException.class, () -> new Pet(null, animalType, birth, owner));
	}

	@Test
	public void testPetStringAnimalTypeDateOwnerNameTooShort() {
		assertThrows(IllegalArgumentException.class, () -> new Pet("", animalType, birth, owner));
	}

	@Test
	public void testPetStringAnimalTypeDateOwnerNameTooLong() {
		assertThrows(IllegalArgumentException.class, () -> new Pet(repeat('A', 101), animalType, birth, owner));
	}
	
	@Test
	public void testPetStringAnimalTypeDateOwnerNullAnimal() {
		assertThrows(NullPointerException.class, () -> new Pet(name, null, birth, owner));
	}
	
	@Test
	public void testPetStringAnimalTypeDateOwnerNullBirth() {
		assertThrows(NullPointerException.class, () -> new Pet(name, animalType, null, owner));
	}
	
	@Test
	public void testPetStringAnimalTypeDateOwnerBirthAfterCurrent() {
		assertThrows(IllegalArgumentException.class, () -> new Pet(name, animalType, futureDate, owner));
	}

	@Test
	public void testSetName() {
		final Pet pet = new Pet(name, animalType, birth);
		
		pet.setName(newName);
		
		assertThat(pet.getName(), is(equalTo(newName)));
	}

	@Test
	public void testSetNameNull() {
		final Pet pet = new Pet(name, animalType, birth);
		
		assertThrows(NullPointerException.class, () -> pet.setName(null));
	}
	
	@Test
	public void testSetNameTooShort() {
		final Pet pet = new Pet(name, animalType, birth);
		
		assertThrows(IllegalArgumentException.class, () -> pet.setName(""));
	}
	
	@Test
	public void testSetNameNullLong() {
		final Pet pet = new Pet(name, animalType, birth);
		
		assertThrows(IllegalArgumentException.class, () -> pet.setName(repeat('A', 101)));
	}

	@Test
	public void testSetAnimal() {
		final Pet pet = new Pet(name, animalType, birth);
		
		pet.setAnimal(newAnimalType);
		
		assertThat(pet.getAnimal(), is(equalTo(newAnimalType)));
	}

	@Test
	public void testSetAnimalNull() {
		final Pet pet = new Pet(name, animalType, birth);
		
		assertThrows(NullPointerException.class, () -> pet.setAnimal(null));
	}

	@Test
	public void testSetBirth() {
		final Pet pet = new Pet(name, animalType, birth);
		
		pet.setBirth(newBirth);
		
		assertThat(pet.getBirth(), is(equalTo(newBirth)));
	}

	@Test
	public void testSetBirthNull() {
		final Pet pet = new Pet(name, animalType, birth);
		
		assertThrows(NullPointerException.class, () -> pet.setBirth(null));
	}

	@Test
	public void testSetBirthAfterCurrent() {
		final Pet pet = new Pet(name, animalType, birth);
		
		assertThrows(IllegalArgumentException.class, () -> pet.setBirth(futureDate));
	}

	@Test
	public void testSetOwner() {
		final Pet pet = new Pet(name, animalType, birth, owner);
		
		pet.setOwner(newOwner);
		
		assertThat(pet.getOwner(), is(equalTo(newOwner)));
		assertThat(newOwner.ownsPet(pet), is(true));
		assertThat(owner.ownsPet(pet), is(false));
	}

	@Test
	public void testSetOwnerNull() {
		final Pet pet = new Pet(name, animalType, birth, owner);
		
		pet.setOwner(null);
		
		assertThat(pet.getOwner(), is(nullValue()));
		assertThat(owner.ownsPet(pet), is(false));
	}
}
