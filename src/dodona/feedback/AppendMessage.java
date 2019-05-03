package dodona.feedback;

public class AppendMessage extends PartialFeedback {

    private Message message;

    public AppendMessage(Message message) {
        super("append-message");
        this.message = message;
    }

}
