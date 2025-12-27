package pablog.petstore.domain.entities;

import org.hamcrest.Matcher;

public class IsEqualToPet extends IsEqualToEntity<Pet> {
	private final boolean checkRelations;
	
	public IsEqualToPet(Pet pet, boolean checkRelations) {
		super(pet);
		this.checkRelations = checkRelations;
	}
	
	@Override
	protected boolean matchesSafely(Pet actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("name", Pet::getName, actual)
				&& checkAttribute("animal", Pet::getAnimal, actual)
				&& checkAttribute("birth", Pet::getBirth, actual)
				&& (!this.checkRelations || checkAttribute("owner", Pet::getOwner, actual, IsEqualToOwner::equalToOwnerWithoutRelations));
		}
	}

	public static IsEqualToPet equalToPet(Pet pet) {
		return new IsEqualToPet(pet, true);
	}
	
	public static IsEqualToPet equalToPetWithoutRelations(Pet pet) {
		return new IsEqualToPet(pet, false);
	}
	
	public static Matcher<Iterable<? extends Pet>> containsPetsInAnyOrder(Pet ... pets) {
		return containsEntityInAnyOrder(IsEqualToPet::equalToPet, pets);
	}
	
	public static Matcher<Iterable<? extends Pet>> containsPetsWithoutRelationsInAnyOrder(Pet ... pets) {
		return containsEntityInAnyOrder(IsEqualToPet::equalToPetWithoutRelations, pets);
	}
	
	public static Matcher<Iterable<? extends Pet>> containsPetsInAnyOrder(Iterable<Pet> pets) {
		return containsEntityInAnyOrder(IsEqualToPet::equalToPet, pets);
	}
	
	public static Matcher<Iterable<? extends Pet>> containsPetsWithoutRelationsInAnyOrder(Iterable<Pet> pets) {
		return containsEntityInAnyOrder(IsEqualToPet::equalToPetWithoutRelations, pets);
	}
}
