package pablog.petstore.rest.entity;

import pablog.petstore.domain.entities.User;

import java.io.Serial;
import java.io.Serializable;

public class UserCredentials implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String login;
    private final String role;

    public UserCredentials(User user) {
        this.login = user.getLogin();
        this.role = user.getRole();
    }

    public String getLogin() {
        return login;
    }

    public String getRole() {
        return role;
    }
}
