package dodona.feedback;

public class DescribedGroup<T> extends Group<T> {

    private Message description = null;

    public void setDescription(Message description) {
        this.description = description;
    }

}
