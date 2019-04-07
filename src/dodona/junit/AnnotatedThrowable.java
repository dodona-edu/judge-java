package dodona.junit;

import dodona.feedback.Message;

public class AnnotatedThrowable extends Throwable {
    private static final long serialVersionUID = 1L;

    private Message message;

    public AnnotatedThrowable(Throwable cause, Message message) {
        super(cause);
        this.message = message;
    }

    public Message getFeedback() {
        return message;
    }

}
