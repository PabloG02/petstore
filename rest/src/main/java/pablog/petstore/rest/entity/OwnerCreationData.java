package pablog.petstore.rest.entity;

import pablog.petstore.domain.entities.Owner;

import java.io.Serial;
import java.io.Serializable;

public class OwnerCreationData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String login;
    private String password;

    protected OwnerCreationData() {
    }

    public OwnerCreationData(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Owner toOwner() {
        return new Owner(this.login, this.password);
    }
}
