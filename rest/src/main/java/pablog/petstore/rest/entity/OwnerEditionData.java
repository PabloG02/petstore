package pablog.petstore.rest.entity;

import pablog.petstore.domain.entities.Owner;

import java.io.Serial;
import java.io.Serializable;

public class OwnerEditionData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String password;

    protected OwnerEditionData() {
    }

    public OwnerEditionData(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void assignData(Owner owner) {
        owner.changePassword(this.password);
    }

}
