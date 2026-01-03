package pablog.petstore.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.identitystore.PasswordHash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;

/**
 * Custom PasswordHash implementation for MD5 hashing.
 * <p>
 * This matches the password hashing strategy used in the User entity.
 * Passwords are hashed using MD5 and stored as uppercase hexadecimal strings.
 * <p>
 * Note: MD5 is used here for compatibility with the existing User entity.
 * For production systems, consider using stronger algorithms like PBKDF2, bcrypt, or Argon2.
 */
@ApplicationScoped
public class MD5PasswordHash implements PasswordHash {

    @Override
    public void initialize(Map<String, String> parameters) {
        // No initialization needed
    }

    @Override
    public String generate(char[] password) {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            String passwordStr = new String(password);
            byte[] hash = digester.digest(passwordStr.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    @Override
    public boolean verify(char[] password, String hashedPassword) {
        String generatedHash = generate(password);
        return generatedHash.equals(hashedPassword);
    }
}
