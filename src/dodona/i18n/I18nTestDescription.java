package dodona.i18n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Sets the description of the test case, which is shown on Dodona as the header
 * of the test case. The value should be a key that is configured in both of the
 * following files:
 * <p>
 * evaluation/properties/descriptions.en.properties
 * evaluation/properties/descriptions.nl.properties
 * <p>
 * Roughly the equivalent of JUnit 5's @DisplayName-annotation, but with i18n
 * support.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nTestDescription {
    /**
     * The resource key of the description to display.
     *
     * @return the resource key
     */
    String value();
}
