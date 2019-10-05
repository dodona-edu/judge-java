package dodona.junit;

import dodona.feedback.Format;
import dodona.feedback.Message;
import dodona.feedback.Permission;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Feedback message writer that allows multiple messages and rewinding of
 * previously sent messages.
 */
public class MultiMessageWriter implements TestRule {
    private final Format defaultFormat;
    private final Permission defaultPermission;
    
    private final List<Message> messages;
    
    /**
     * MultiMessageWriter constructor.
     *
     * @param defaultPermission the default permission for messages
     * @param defaultFormat     the default format for messages
     */
    public MultiMessageWriter(final Permission defaultPermission, final Format defaultFormat) {
        this.defaultFormat = defaultFormat;
        this.defaultPermission = defaultPermission;
        this.messages = new ArrayList<>();
    }
    
    /**
     * MultiMessageWriter constructor. Assumes student-level code messages.
     */
    public MultiMessageWriter() {
        this(Permission.STUDENT, Format.CODE);
    }
    
    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (final Throwable e) {
                    throw new AnnotatedThrowable(e, MultiMessageWriter.this.getMessages());
                }
            }
        };
    }
    
    /**
     * Appends the given message to the list of messages.
     *
     * @param message the message to append
     * @return the index in the list
     */
    public int append(final Message message) {
        this.messages.add(message);
        return this.messages.size() - 1;
    }
    
    /**
     * Creates a message with the given contents and appends it to the list of
     * messages.
     *
     * @param content the contents of the message to append
     * @return the index in the list
     */
    public int append(final String content) {
        return this.append(new Message(this.defaultFormat, content, this.defaultPermission));
    }
    
    /**
     * Get all feedback messages.
     *
     * @return the feedback messages
     */
    public List<Message> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }
    
    /**
     * Removes the message at the given index.
     *
     * @param idx the index of the message to remove
     */
    public void remove(final int idx) {
        this.messages.remove(idx);
    }
    
    /**
     * Removes all messages matching the given predicate.
     *
     * @param condition the condition to match
     */
    public void removeIf(final Predicate<Message> condition) {
        this.messages.removeIf(condition);
    }
    
    /**
     * Gets the amount of messages currently added.
     *
     * @return the amount of messages
     */
    public int size() {
        return this.messages.size();
    }
}
