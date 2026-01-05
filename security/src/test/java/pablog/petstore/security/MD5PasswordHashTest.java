package pablog.petstore.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MD5PasswordHashTest {

    private final MD5PasswordHash passwordHash = new MD5PasswordHash();

    @Test
    void testGenerateBasicHash() {
        char[] password = "password123".toCharArray();

        String hash = passwordHash.generate(password);

        assertNotNull(hash);
        assertEquals(32, hash.length(), "MD5 hash should be 32 characters (128 bits in hex)");
        assertTrue(hash.matches("[A-F0-9]+"), "Hash should only contain uppercase hex characters");
    }

    @ParameterizedTest
    @CsvSource({
            "password, 5F4DCC3B5AA765D61D8327DEB882CF99",
            "admin, 21232F297A57A5A743894A0E4A801FC3",
            "test, 098F6BCD4621D373CADE4E832627B4F6",
            "123456, E10ADC3949BA59ABBE56E057F20F883E",
            "'', D41D8CD98F00B204E9800998ECF8427E"
    })
    void testGenerateKnownHashes(String password, String expectedHash) {
        char[] passwordChars = password.toCharArray();

        String actualHash = passwordHash.generate(passwordChars);

        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testGenerateConsistentHashes() {
        char[] password = "consistentPassword".toCharArray();

        String hash1 = passwordHash.generate(password);
        String hash2 = passwordHash.generate(password);

        assertEquals(hash1, hash2);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "special!@#$%^&*()",
            "unicode-日本語-test",
            "spaces in password",
            "UPPERCASE",
            "MiXeDcAsE",
            "numbers123456789"
    })
    void testGenerateVariousFormats(String password) {
        char[] passwordChars = password.toCharArray();

        String hash = passwordHash.generate(passwordChars);

        assertNotNull(hash);
        assertEquals(32, hash.length());
        assertTrue(hash.matches("[A-F0-9]+"));
    }

    @Test
    void testVerifyCorrectPassword() {
        char[] password = "correctPassword".toCharArray();
        String hash = passwordHash.generate(password);

        boolean result = passwordHash.verify(password, hash);

        assertTrue(result);
    }

    @Test
    void testVerifyIncorrectPassword() {
        char[] correctPassword = "correctPassword".toCharArray();
        char[] wrongPassword = "wrongPassword".toCharArray();
        String hash = passwordHash.generate(correctPassword);

        boolean result = passwordHash.verify(wrongPassword, hash);

        assertFalse(result);
    }

    @Test
    void testVerifyCaseSensitive() {
        char[] password = "Password".toCharArray();
        char[] differentCase = "password".toCharArray();
        String hash = passwordHash.generate(password);

        boolean correctCase = passwordHash.verify(password, hash);
        boolean wrongCase = passwordHash.verify(differentCase, hash);

        assertTrue(correctCase);
        assertFalse(wrongCase);
    }

    @Test
    void testVerifyWithInvalidHash() {
        char[] password = "password".toCharArray();
        String invalidHash = "not-a-valid-hash";

        boolean result = passwordHash.verify(password, invalidHash);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject verification with lowercase hash")
    void testVerifyWithLowercaseHash() {
        char[] password = "password".toCharArray();
        String uppercaseHash = passwordHash.generate(password);
        String lowercaseHash = uppercaseHash.toLowerCase();

        boolean result = passwordHash.verify(password, lowercaseHash);

        assertFalse(result, "Verification should fail for lowercase hash");
    }

    @Test
    @DisplayName("Should handle null-like edge cases gracefully")
    void testVerifyWithEmptyStrings() {
        char[] emptyPassword = new char[0];
        String emptyHash = passwordHash.generate(emptyPassword);

        boolean result = passwordHash.verify(emptyPassword, emptyHash);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should maintain hash format across multiple operations")
    void testMultipleOperations() {
        char[] password = "multipleOps".toCharArray();

        for (int i = 0; i < 100; i++) {
            String hash = passwordHash.generate(password);
            assertEquals(32, hash.length());
            assertTrue(hash.matches("[A-F0-9]+"));
            assertTrue(passwordHash.verify(password, hash));
        }
    }

    @Test
    @DisplayName("Should handle very long passwords")
    void testLongPassword() {
        String longPasswordStr = "a".repeat(10000);
        char[] longPassword = longPasswordStr.toCharArray();

        String hash = passwordHash.generate(longPassword);

        assertNotNull(hash);
        assertEquals(32, hash.length());
        assertTrue(passwordHash.verify(longPassword, hash));
    }
}