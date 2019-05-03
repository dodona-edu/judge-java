package dodona.feedback;

public class CloseTest extends AcceptedClose {

    private String generated;
    private Status status;

    public CloseTest(String generated, Status status, boolean accepted) {
        super("close-test", accepted);
        this.generated = generated;
        this.status = status;
    }

}
