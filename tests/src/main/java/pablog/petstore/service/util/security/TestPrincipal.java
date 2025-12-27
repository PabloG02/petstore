package pablog.petstore.service.util.security;

import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;

import java.security.Principal;

@Alternative
@Singleton
public class TestPrincipal implements Principal {
    private String name;

    public TestPrincipal() {
    }

    public TestPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}