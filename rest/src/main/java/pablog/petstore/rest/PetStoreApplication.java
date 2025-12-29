package pablog.petstore.rest;

import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class PetStoreApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Resources
        classes.add(OwnerResource.class);
        classes.add(PetResource.class);
        classes.add(UserResource.class);
        // Providers
        classes.add(CORSFilter.class);
        classes.add(IllegalArgumentExceptionMapper.class);
        classes.add(SecurityExceptionMapper.class);
        return classes;
    }
}