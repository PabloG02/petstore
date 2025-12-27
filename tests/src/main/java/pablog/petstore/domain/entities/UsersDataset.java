package pablog.petstore.domain.entities;

public class UsersDataset {
	public static final String EXISTENT_LOGIN = "jose";

	public static User[] users() {
	  final Owner[] owners = OwnersDataset.owners();
	  final User[] users = new User[owners.length + 1];
	  
	  users[0] = new Administrator(EXISTENT_LOGIN, EXISTENT_LOGIN + "pass");
	  System.arraycopy(owners, 0, users, 1, owners.length);
	  
	  return users;
	}
	
	public static User existentAdmin() {
	  return users()[0];
	}
}
