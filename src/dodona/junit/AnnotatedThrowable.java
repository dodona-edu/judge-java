package dodona.junit;

import dodona.feedback.Message;

import java.util.Collections;
import java.util.List;

public class AnnotatedThrowable extends Throwable {
    private static final long serialVersionUID = 1L;

    private final List<Message> messages;

    /**
     * AnnotatedThrowable constructor.
     *
     * @param cause    the test failure cause
     * @param messages the feedback messages
     */
    public AnnotatedThrowable(final Throwable cause, final List<Message> messages) {
        super(cause);
        this.messages = messages;
    }

    /**
     * AnnotatedThrowable constructor.
     *
     * @param cause   the test failure cause
     * @param message the feedback message
     */
    public AnnotatedThrowable(final Throwable cause, final Message message) {
        this(cause, Collections.singletonList(message));
    }

    /**
     * Gets the feedback messages.
     *
     * @return the feedback messages
     */
    public List<Message> getFeedback() {
        return Collections.unmodifiableList(this.messages);
    }

}
