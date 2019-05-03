package dodona.feedback;

public abstract class AcceptedClose extends PartialFeedback {

    private Boolean accepted;

    public AcceptedClose(String command, Boolean accepted) {
        super(command);
        this.accepted = accepted;
    }

}
