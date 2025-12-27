package pablog.petstore.domain.entities;

import org.hamcrest.Matcher;

public class IsEqualToUser extends IsEqualToEntity<User> {
	public IsEqualToUser(User user) {
		super(user);
	}

	@Override
	protected boolean matchesSafely(User actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("login", User::getLogin, actual)
				&& checkAttribute("role", User::getRole, actual);
		}
	}

	public static IsEqualToUser equalToUser(User user) {
		return new IsEqualToUser(user);
	}
	
	public static Matcher<Iterable<? extends User>> containsUsersInAnyOrder(User ... users) {
		return containsEntityInAnyOrder(IsEqualToUser::equalToUser, users);
	}
	
	public static Matcher<Iterable<? extends User>> containsUsersInAnyOrder(Iterable<User> users) {
		return containsEntityInAnyOrder(IsEqualToUser::equalToUser, users);
	}
}
