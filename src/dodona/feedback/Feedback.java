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

    public void deriveDescription() {
        long failed = children().mapToInt(Tab::getBadgeCount).sum();
        long executed = children().mapToLong(t -> t.children().count()).sum();
        switch(status) {
            case TIME_LIMIT_EXCEEDED:
                this.description = executed + " tests uitgevoerd";
                break;
            case CORRECT:
                this.description = (executed - failed) + " tests geslaagd";
                break;
            default:
                this.description = failed + " tests mislukt";
                break;
        }
    }

}
