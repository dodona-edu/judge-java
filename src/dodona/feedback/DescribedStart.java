package dodona.feedback;

public class DescribedStart extends PartialFeedback {

    private Message description;

    protected DescribedStart(String command, Message description) {
        super(command);
        this.description = description;
    }

}
