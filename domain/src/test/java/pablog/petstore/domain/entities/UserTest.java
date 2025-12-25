package pablog.petstore.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class UserTest {
	protected String login;
	protected String password;
	protected String md5Pass;
	
	protected String newLogin;
	protected String newPassword;
	protected String newPasswordMD5;
	
	protected String shortLogin;
	protected String longLogin;
	protected String shortPassword;

	@BeforeEach
	public void setUpUser() throws Exception {
		this.login = "Pepe";
		this.password = "pepepa";
		this.md5Pass = "41B0EEB2550AE3A43BF34DC2E8408E90";
		
		this.newLogin = "José";
		this.newPassword = "josepass";
		this.newPasswordMD5 = "A3F6F4B40B24E2FD61F08923ED452F34";
		
		this.shortLogin = "";
		this.longLogin = repeat('A', 101); 
		this.shortPassword = repeat('A', 5);
	}
	
	protected abstract User newUser(String login, String password);

	@Test
	public void testUserStringString() {
		final String[] logins = { login, "A", repeat('A', 100) };
		
		for (String login : logins) {
			final User admin = newUser(login, password);
	
			assertThat(admin.getLogin(), is(equalTo(login)));
			assertThat(admin.getPassword(), is(equalTo(md5Pass)));
		}
	}

	@Test
	public void testUserStringStringNullLogin() {
		assertThrows(NullPointerException.class, () -> newUser(null, password));
	}
	
	@Test
	public void testUserStringStringNullPassword() {
		assertThrows(NullPointerException.class, () -> newUser(login, null));
	}
	
	@Test
	public void testUserStringStringLoginTooShort() {
		assertThrows(IllegalArgumentException.class, () -> newUser(shortLogin, password));
	}
	
	@Test
	public void testUserStringStringLoginTooLong() {
		assertThrows(IllegalArgumentException.class, () -> newUser(longLogin, password));
	}
	
	@Test
	public void testUserStringStringPasswordTooShort() {
		assertThrows(IllegalArgumentException.class, () -> newUser(login, shortPassword));
	}
	
	@Test
	public void testSetLogin() {
		final String[] logins = { login, "A", repeat('A', 100) };
		
		for (String login : logins) {
			final User admin = newUser(login, password);
			
			admin.setLogin(newLogin);
	
			assertThat(admin.getLogin(), is(equalTo(newLogin)));
		}
	}

	@Test
	public void testSetLoginTooShort() {
		final User admin = newUser(login, password);
		
		assertThrows(IllegalArgumentException.class, () -> admin.setLogin(shortLogin));
	}

	@Test
	public void testSetLoginTooLong() {
		final User admin = newUser(login, password);
		
		assertThrows(IllegalArgumentException.class, () -> admin.setLogin(longLogin));
	}

	@Test
	public void testSetPassword() {
		final User admin = newUser(login, password);
		
		admin.setPassword(newPasswordMD5);

		assertThat(admin.getPassword(), is(equalTo(newPasswordMD5)));
	}

	@Test
	public void testSetPasswordNoMD5() {
		final User admin = newUser(login, password);
		
		assertThrows(IllegalArgumentException.class, () -> admin.setPassword("No MD5 password"));
	}

	@Test
	public void testSetPasswordNullValue() {
		final User admin = newUser(login, password);
		
		assertThrows(NullPointerException.class, () -> admin.setPassword(null));
	}

	@Test
	public void testChangePassword() {
		final User admin = newUser(login, password);
		
		admin.changePassword(newPassword);

		assertThat(admin.getPassword(), is(equalTo(newPasswordMD5)));
	}

	@Test
	public void testChangePasswordNull() {
		final User admin = newUser(login, password);
		
		assertThrows(NullPointerException.class, () -> admin.changePassword(null));
	}

	@Test
	public void testChangePasswordTooShort() {
		final User admin = newUser(login, password);
		
		assertThrows(IllegalArgumentException.class, () -> admin.changePassword(shortPassword));
	}
}
