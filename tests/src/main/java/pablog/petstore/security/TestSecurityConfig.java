package pablog.petstore.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

/**
 * Jakarta Security identity store configuration for tests.
 */
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "java:jboss/datasources/ExampleDS",
        callerQuery = "SELECT password FROM users WHERE login = ?",
        groupsQuery = "SELECT role FROM users WHERE login = ?",
        hashAlgorithm = MD5PasswordHash.class,
        priority = 10
)
@ApplicationScoped
public class TestSecurityConfig {
}
