package dodona.feedback;

public enum Permission {
    STUDENT, /* visible for everyone */
    STAFF,   /* visible for staff members */
    ZEUS;    /* visible only for almighty Zeus */

    public String toString() {
        return super.toString().toLowerCase();
    }
}
