package dodona.feedback;

public class CloseJudgement extends AcceptedClose {

    private Status status;

    public CloseJudgement(Status status, Boolean accepted) {
        super("close-judgement", accepted);
        this.status = status;
    }

    public CloseJudgement(Status status) {
        this(status, null);
    }

    public CloseJudgement(Boolean accepted) {
        this(null, accepted);
    }

    public CloseJudgement() {
        this(null, null);
    }

}
