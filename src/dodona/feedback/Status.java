package dodona.feedback;

public enum Status {
    CORRECT(0),
    WRONG(1),
    TIME_LIMIT_EXCEEDED(4),
    MEMORY_LIMIT_EXCEEDED(4),
    RUNTIME_ERROR(2),
    INTERNAL_ERROR(3);

    private final int severity;

    Status(int severity) {
        this.severity = severity;
    }

    public static Status worse(Status a, Status b) {
        return a.severity > b.severity ? a : b;
    }
}
