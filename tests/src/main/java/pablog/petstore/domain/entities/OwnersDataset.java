package pablog.petstore.domain.entities;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

public class OwnersDataset {
	public static final String EXISTENT_LOGIN = "pepe";
	public static final String NON_EXISTENT_LOGIN = "non-existent";
	public static final String OWNER_WITH_PETS_LOGIN = "juan";
	public static final String OWNER_WITHOUT_PETS_LOGIN = "lorena";

	public static Owner ownerWithLogin(String login) {
		return stream(owners())
			.filter(owner -> owner.getLogin().equals(login))
			.findFirst()
		.orElseThrow(IllegalArgumentException::new);
	}
	
	public static Owner[] owners(String ... logins) {
		final Set<String> loginSet = stream(logins).collect(toSet());
		
		return stream(owners())
			.filter(owner -> loginSet.contains(owner.getLogin()))
		.toArray(Owner[]::new);
	}
	
	public static Owner[] owners() {
		return new Owner[] {
			new Owner(EXISTENT_LOGIN, EXISTENT_LOGIN + "pass",
				new Pet(1, "Pepecat", AnimalType.CAT, new Date(946684861000L))),
			new Owner(OWNER_WITH_PETS_LOGIN, OWNER_WITH_PETS_LOGIN + "pass",
				new Pet(2, "Max", AnimalType.CAT, new Date(946684861000L)),
				new Pet(3, "Juandog", AnimalType.DOG, new Date(946684861000L))
			),
			new Owner("ana", "anapass",
				new Pet(4, "Anacat", AnimalType.CAT, new Date(946684861000L)),
				new Pet(5, "Max", AnimalType.DOG, new Date(946684861000L)),
				new Pet(6, "Anabird", AnimalType.BIRD, new Date(946684861000L))
			),
			new Owner(OWNER_WITHOUT_PETS_LOGIN, OWNER_WITHOUT_PETS_LOGIN + "pass")
		};
	}
	
	public static Owner[] ownersAnd(Owner ... additionalOwners) {
		final Owner[] owners = owners();
		final Owner[] ownersWithNewOwner = new Owner[owners.length + additionalOwners.length];
		
		System.arraycopy(owners, 0, ownersWithNewOwner, 0, owners.length);
		System.arraycopy(additionalOwners, 0, ownersWithNewOwner, owners.length, additionalOwners.length);
		
		return ownersWithNewOwner;
	}
	
	public static Owner[] ownersWithout(Owner ... removeOwners) {
		final List<Owner> owners = new ArrayList<>(asList(owners()));

		for (Owner owner : removeOwners) {
			final Iterator<Owner> itOwner = owners.iterator();
			
			while (itOwner.hasNext()) {
				if (itOwner.next().getLogin().equals(owner.getLogin())) {
					itOwner.remove();
					break;
				}
			}
		}
		
		return owners.toArray(new Owner[owners.size()]);
	}
	
	public static String petNameWithMultipleOwners() {
		return "Max";
	}
	
	public static String petNameWithSingleOwner() {
		return "Juandog";
	}
	
	public static Owner[] ownersOf(String petName) {
		final List<Owner> owners = new ArrayList<>();
		
		for (Owner owner : owners()) {
			for (Pet pet : owner.getPets()) {
				if (pet.getName().equals(petName)) {
					owners.add(owner);
					break;
				}
			}
		}
		
		return owners.toArray(new Owner[owners.size()]);
	}
	
	public static Pet petWithId(int id) {
		return stream(pets())
			.filter(pet -> pet.getId() == id)
			.findFirst()
		.orElseThrow(IllegalArgumentException::new);
	}
	
	public static Pet[] pets() {
		return stream(owners())
			.map(Owner::getPets)
			.flatMap(Collection::stream)
		.toArray(Pet[]::new);
	}
	
	public static Owner newOwnerWithoutPets() {
		return new Owner(newOwnerLogin(), newOwnerPassword());
	}
	
	public static Owner newOwnerWithFreshPets() {
    return new Owner(newOwnerLogin(), newOwnerPassword(),
      new Pet("Jacintocat", AnimalType.CAT, new Date(946684861000L)),
      new Pet("Jacintodo", AnimalType.DOG, new Date(946684861000L)),
      new Pet("Jacintobird", AnimalType.BIRD, new Date(946684861000L))
    );
  }
  
  public static Owner newOwnerWithPersistentPets() {
    return new Owner(newOwnerLogin(), newOwnerPassword(),
      new Pet(7, "Jacintocat", AnimalType.CAT, new Date(946684861000L)),
      new Pet(8, "Jacintodo", AnimalType.DOG, new Date(946684861000L)),
      new Pet(9, "Jacintobird", AnimalType.BIRD, new Date(946684861000L))
    );
  }
	
	public static String newOwnerLogin() {
		return "jacinto";
	}
	
	public static String newOwnerPassword() {
		return "jacintopass";
	}
	
	public static String anyLogin() {
		return existentLogin();
	}
	
	public static String existentLogin() {
		return EXISTENT_LOGIN;
	}

  public static String existentPassword() {
    return EXISTENT_LOGIN + "pass";
  }
	
	public static String nonExistentLogin() {
		return NON_EXISTENT_LOGIN;
	}
	
	public static Owner anyOwner() {
		return ownerWithLogin(anyLogin());
	}
	
	public static String anyOwnerPassword() {
	  return anyOwner().getLogin() + "pass";
	}

	public static Owner existentOwner() {
		return ownerWithLogin(existentLogin());
	}
	
	public static String newPasswordForExistentOwner() {
		return "newpassword";
	}

	public static Owner nonExistentOwner() {
		return new Owner(nonExistentLogin(), nonExistentLogin() + "pass");
	}
	
	public static Owner ownerWithPets() {
		return ownerWithLogin(OWNER_WITH_PETS_LOGIN);
	}
	
	public static Owner ownerWithoutPets() {
		return ownerWithLogin(OWNER_WITHOUT_PETS_LOGIN);
	}
	
	public static Pet anyPetOf(Owner owner) {
		if (owner.getPets().isEmpty())
			throw new IllegalArgumentException("owner doesn't have pets");
		
		return owner.getPets().iterator().next();
	}
	
	public static Pet anyPet() {
		return petWithId(existentPetId());
	}
	
	public static Pet newPet() {
		return newPetWithOwner(null);
	}
	
	public static Pet newPetWithOwner(Owner owner) {
		return new Pet("Lorenacat", AnimalType.CAT, new Date(946684861000L), owner);
	}
	
	public static String existentPetName() {
		return "Pepecat";
	}
	
	public static String nonExistentPetName() {
		return "NonExistentPet";
	}
	
	public static int existentPetId() {
		return 2;
	}
	
	public static Pet existentPet() {
		return petWithId(existentPetId());
	}
	
	public static int nonExistentPetId() {
		return 1000000;
	}
}
