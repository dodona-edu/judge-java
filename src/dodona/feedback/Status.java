package dodona.feedback;

public enum Status {
    CORRECT,
    WRONG,
    TIME_LIMIT_EXCEEDED,
    RUNTIME_ERROR,
    MEMORY_LIMIT_EXCEEDED;

    public String toString() {
        return super.toString().toLowerCase().replace('_', ' ');
    }
}
