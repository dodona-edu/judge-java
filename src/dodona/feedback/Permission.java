package dodona.feedback;

public enum Permission {
    STUDENT, /* visible for everyone */
    EDUCATOR, /* visible for educators */
    @Deprecated
    STAFF,    /* use EDUCATOR; kept so existing exercise tests still compile */
    ZEUS;     /* visible only for almighty Zeus */
}
