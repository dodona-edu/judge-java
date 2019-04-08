package dodona.feedback;

import static dodona.util.TextUtil.pluralize;

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
                this.description = executed + " " + pluralize(executed, "test", "tests") + " uitgevoerd";
                break;
            case CORRECT:
                this.description = (executed - failed) + " " + pluralize(executed - failed, "test", "tests") + " geslaagd";
                break;
            default:
                this.description = failed + " " + pluralize(failed, "test", "tests") + " mislukt";

                break;
        }
    }

}
