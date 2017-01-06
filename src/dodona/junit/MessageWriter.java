package dodona.junit;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import dodona.feedback.Message;
import dodona.feedback.Permission;
import dodona.feedback.Format;

public class MessageWriter extends PrintWriter implements TestRule {

    private Permission permission;
    private Format format;
    private StringWriter writer;

    public MessageWriter() {
        this(Permission.STUDENT, Format.CODE);
    }

    public MessageWriter(Permission permission, Format format) {
        this(permission, format, new StringWriter());
    }

    private MessageWriter(Permission permission, Format format, StringWriter writer) {
        super(writer);
        this.permission = permission;
        this.format = format;
        this.writer = writer;
    }

    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable e) {
                    throw new AnnotatedThrowable(e, new Message(format, writer.toString(), permission));
                }
            }
        };
    }

}
