package dodona.feedback;

public class CloseContext extends AcceptedClose {

    public CloseContext(Boolean accepted) {
        super("close-context", accepted);
    }

    public CloseContext() {
        this(null);
    }

}
