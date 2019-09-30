package dodona.feedback;

public class CloseTest extends AcceptedClose {

    private String generated;
    private StatusPair status;

    public CloseTest(String generated, StatusPair status, boolean accepted) {
        super("close-test", accepted);
        this.generated = generated;
        this.status = status;
    }

}
