package pablog.petstore.domain.entities;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.matchesPattern;

@Entity
@Inheritance
@DiscriminatorColumn(
        name = "role",
        discriminatorType = DiscriminatorType.STRING,
        length = 5
)
public abstract class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 100, nullable = false)
    protected String login;

    @Column(length = 32, nullable = false)
    protected String password;

    @Column(name = "role", insertable = false, updatable = false)
    protected String role;

    protected User() {
    }

    /**
     * Creates a new instance of {@code User}.
     *
     * @param login    the login that identifies the user. This parameter must be a
     *                 non-empty and non {@code null} string with a maximum length of 100 chars.
     * @param password the raw password of the user. This parameter must be a
     *                 non {@code null} string with a minimum length of 6 chars.
     * @throws NullPointerException     if a {@code null} value is passed as the
     *                                  value for any parameter.
     * @throws IllegalArgumentException if value provided for any parameter is
     *                                  not valid according to its description.
     */
    public User(String login, String password) {
        this.setLogin(login);
        this.changePassword(password);
    }

    /**
     * Returns the login of this user.
     *
     * @return the login of this user.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets the login of this user.
     *
     * @param login the login that identifies the user. This parameter
     *              must be a non-empty and non {@code null} string with a maximum length of
     *              100 chars.
     * @throws NullPointerException     if {@code null} is passed as parameter.
     * @throws IllegalArgumentException if the length of the string passed is
     *                                  not valid.
     */
    public void setLogin(String login) {
        requireNonNull(login, "login can't be null");
        inclusiveBetween(1, 100, login.length(), "login must have a length between 1 and 100");

        this.login = login;
    }

    /**
     * Returns the role of the user. This value is automatically set by JPA, as
     * it is the value used as discriminator in the inheritance.
     *
     * @return the role of the user.
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the MD5 of the user's password. Capital letters are used
     * in the returned string.
     *
     * @return the MD5 of the user's password. Capital letters are used
     * in the returned string.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the MD5 password of the user. The MD5 string is stored with
     * capital letters.
     *
     * @param password the MD5 password of the user. This parameter must be a
     *                 non {@code null} MD5 string.
     * @throws NullPointerException     if {@code null} is passed as parameter.
     * @throws IllegalArgumentException if the string passed is not a valid MD5
     *                                  string.
     */
    public void setPassword(String password) {
        requireNonNull(password, "password can't be null");
        matchesPattern(password, "[a-zA-Z0-9]{32}", "password must be a valid MD5 string");

        this.password = password.toUpperCase();
    }

    /**
     * Changes the password of the user. This method receives the raw value of
     * the password and stores it in MD5 format.
     *
     * @param password the raw password of the user. This parameter must be a
     *                 non {@code null} string with a minimum length of 6 chars.
     * @throws NullPointerException     if the {@code password} is {@code null}.
     * @throws IllegalArgumentException if the length of the string passed is
     *                                  not valid.
     */
    public void changePassword(String password) {
        requireNonNull(password, "password can't be null");
        if (password.length() < 6)
            throw new IllegalArgumentException("password can't be shorter than 6");

        try {
            final MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] hash = digester.digest(password.getBytes(StandardCharsets.UTF_8));
            this.password = HexFormat.of().formatHex(hash).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}