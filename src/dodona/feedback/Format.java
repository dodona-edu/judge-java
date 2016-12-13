package dodona.feedback;

public enum Format {
    PLAIN,      /* Formatted as plain text */
    HTML,       /* HTML markup is not escaped */
    MARKDOWN,   /* Converted to HTML using Cramdown */
    CODE,       /* Preserves whitespace and rendered in monospace */
    PYTHON,     /* CODE, with python highlighting */
    JAVASCRIPT; /* CODE, with javascript highlighting */

    public String toString() {
        return super.toString().toLowerCase();
    }
}
