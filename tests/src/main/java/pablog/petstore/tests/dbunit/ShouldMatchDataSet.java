package pablog.petstore.tests.dbunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify expected dataset files to compare against after test execution.
 * Replicates the behavior of Arquillian Persistence Extension's @ShouldMatchDataSet.
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@literal @}ShouldMatchDataSet(value = "expected.xml", excludeColumns = {"id", "created_at"})
 * public void testMethod() { ... }
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShouldMatchDataSet {
    /**
     * Dataset file names containing expected state. Files are loaded from classpath under "datasets/" directory.
     */
    String[] value();

    /**
     * Columns to exclude from comparison (useful for auto-generated IDs, timestamps, etc.).
     */
    String[] excludeColumns() default {};

    /**
     * Tables to order by their primary key before comparison.
     * This helps with consistent ordering when comparing datasets.
     */
    String[] orderBy() default {};
}
