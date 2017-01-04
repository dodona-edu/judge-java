package dodona.junit;

import java.util.List;
import java.util.ArrayList;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import dodona.feedback.Message;
import dodona.feedback.Permission;
import dodona.feedback.Format;

public class Messenger implements TestRule {

    private List<Message> messages;
    private Permission permission;

    public Messenger() {
        this(Permission.STUDENT);
    }

    public Messenger(Permission permission) {
        this.permission = permission;
        this.messages = new ArrayList<>();
    }

    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable e) {
                    throw new AnnotatedThrowable(e, messages);
                }
            }
        };
    }

    public void send(Message message) {
        messages.add(message);
    }

    public void send(String message) {
        messages.add(new Message(Format.CODE, message, permission));
    }

}
