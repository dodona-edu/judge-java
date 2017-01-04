package dodona.junit;

import java.util.List;

import dodona.feedback.Message;

public class AnnotatedThrowable extends Throwable {
    private static final long serialVersionUID = 1L;

    private List<Message> messages;

    public AnnotatedThrowable(Throwable cause, List<Message> messages) {
        super(cause);
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

}
