package pablog.petstore.tests.dbunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify dataset files to load before test execution.
 * Replicates the behavior of Arquillian Persistence Extension's @UsingDataSet.
 * 
 * <p>Can be applied at class level (applies to all tests) or method level (overrides class level).</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@literal @}UsingDataSet("owners.xml")
 * public void testMethod() { ... }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsingDataSet {
    /**
     * Dataset file names to load. Files are loaded from the classpath under "datasets/" directory.
     * Multiple files are merged in the order specified.
     */
    String[] value();
}
