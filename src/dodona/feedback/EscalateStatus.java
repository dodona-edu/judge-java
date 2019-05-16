package dodona.feedback;

public class EscalateStatus extends PartialFeedback {

    private StatusPair status;

    public EscalateStatus(Status enum_, String human) {
        super("escalate-status");
        this.status = new StatusPair(enum_, human);
    }

}
