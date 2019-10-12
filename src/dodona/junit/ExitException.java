package dodona.junit;

public class ExitException extends SecurityException {

    private final int status;

    public ExitException(int status) {
        super(String.format("System.exit(%d) called.", status));
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}