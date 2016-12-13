package dodona.feedback;

public class Feedback extends Group<Tab> {

    private Status status = Status.CORRECT;
    private boolean accepted = true;
    private String description;

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

}
