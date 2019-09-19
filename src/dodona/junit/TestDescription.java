package dodona.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Sets the description of the test case, which is shown on Dodona as the header
 * of the test case.
 * <p>
 * Roughly the equivalent of JUnit 5's @DisplayName-annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TestDescription {
    /**
     * The description to display.
     *
     * @return the description
     */
    String value();
}
