package dodona.junit;

import dodona.feedback.Message;
import dodona.feedback.Status;
import dodona.feedback.StartTest;
import dodona.feedback.CloseTest;

import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TestWriter implements TestRule {

    private StartTest start;
    private CloseTest close;

    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable e) {
                    if(start != null) {
                        throw new TestCarryingThrowable(e, start, close);
                    } else {
                        throw e;
                    }
                }
            }
        };
    }

    public void compare(Object expected, Object received) {
        compare(null, expected, received);
    }

    public void compare(String description, Object expected, Object received) {
        start = new StartTest(
            expected == null ? "<null>" : expected.toString(),
            description == null ? null : Message.code(description)
        );
        close = new CloseTest(
            received == null ? "<null>" : received.toString(),
            Status.WRONG, /* crashed tests won't even get this far */
            false /* This is only visible for failed tests, so we can assume this. */
        );
        if(expected == null && received != null) Assert.fail();
        if(expected != null && !expected.equals(received)) Assert.fail();
    }

}
