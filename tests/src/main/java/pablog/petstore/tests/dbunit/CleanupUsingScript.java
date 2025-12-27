package pablog.petstore.tests.dbunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify SQL scripts to execute for database cleanup after test execution.
 * Replicates the behavior of Arquillian Persistence Extension's @CleanupUsingScript.
 * 
 * <p>Can be applied at class level (applies to all tests) or method level (overrides class level).</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@literal @}CleanupUsingScript({"cleanup.sql", "cleanup-autoincrement.sql"})
 * public void testMethod() { ... }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CleanupUsingScript {
    /**
     * SQL script file names to execute for cleanup. Files are loaded from the classpath.
     */
    String[] value();
}
