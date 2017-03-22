package dodona.feedback;

public class Feedback extends Group<Tab> {

    private Status status = Status.CORRECT;
    private boolean accepted = true;
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

    public void worseStatus(Status status) {
        if(status.ordinal() > this.status.ordinal()) {
            this.status = status;
        }
    }

}
