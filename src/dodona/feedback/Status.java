package dodona.feedback;

public enum Status {
    // order (better to worse) matters!
    CORRECT,
    WRONG,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    RUNTIME_ERROR;
}
