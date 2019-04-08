package dodona.util;

/**
 * Utilities for text.
 */
public enum TextUtil {;

    /**
     * Pluralizes the given string.
     *
     * @param amount   the amount of items
     * @param singular text to return in case of plural
     * @param plural   text to return in case of plural
     * @return either the singular or the plural text, depending on amount
     */
    public static String pluralize(long amount, final String singular, final String plural) {
        return amount == 1 ? singular : plural;
    }
}
