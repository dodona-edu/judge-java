package dodona.feedback;

public class Feedback extends Group<Tab> {

    private Status status = Status.INTERNAL_ERROR;
    private boolean accepted = false;
    private String description;

    public boolean isStatus(Status status) {
        return this.status == status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

}
