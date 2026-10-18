package dodona.junit;

// An Error, so a submission's catch (Exception e) doesn't swallow it.
public class SystemExitError extends Error {
    public SystemExitError(String method, int status) {
        super("Your code called " + method + "(" + status + ").");
    }
}
