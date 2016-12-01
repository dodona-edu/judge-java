package dodona.feedback;

public class CheckedGroup<T> extends DescribedGroup<T> {

    private boolean accepted = false;

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

}
