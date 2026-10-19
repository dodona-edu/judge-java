package dodona.junit;

// An Error, so a submission's catch (Exception e) doesn't swallow it.
public class SystemExitError extends Error {
    private final int status;

    public SystemExitError(String method, int status) {
        super("Your code called " + method + "(" + status + ").");
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
