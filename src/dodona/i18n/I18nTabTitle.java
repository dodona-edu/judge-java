package dodona.i18n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Sets the title of the tab, which is shown on Dodona on top with a badge
 * displaying the amount of failed test cases. The value should be a key that is
 * configured in both of the following files:
 * <p>
 * evaluation/properties/descriptions.en.properties
 * evaluation/properties/descriptions.nl.properties
 * <p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nTabTitle {
    /**
     * The resource key of the description to display.
     *
     * @return the resource key
     */
    String value();
}
