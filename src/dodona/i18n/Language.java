package dodona.i18n;

import java.util.Arrays;
import java.util.Objects;

import static dodona.junit.JUnitJSON.PROPERTY_LANGUAGE;

/**
 * Supported languages on Dodona.
 */
public enum Language {
    DUTCH("nl"),
    ENGLISH("en");
    
    private final String identifier;
    
    /**
     * Language constructor.
     *
     * @param identifier 2-letter identifier of the language
     */
    Language(final String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Gets the current language. Defaults to English if an unknown language was
     * passed.
     *
     * @return the current active language
     */
    public static Language current() {
        final String fromProperties = System.getProperty(PROPERTY_LANGUAGE);
        return Arrays.stream(Language.values())
            .filter(lang -> Objects.equals(fromProperties, lang.identifier))
            .findAny()
            .orElse(ENGLISH);
    }
    
    /**
     * Gets the 2-letter identifier.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return this.identifier;
    }
}
