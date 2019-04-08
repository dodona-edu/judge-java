package dodona.junit;

import dodona.feedback.Message;
import dodona.feedback.Test;

import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TestWriter implements TestRule {

    private Test test;

    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable e) {
                    if(test != null) {
                        throw new TestCarryingThrowable(e, test);
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
        test = new Test(
            description == null ? null : Message.code(description),
            false, /* This is only visible for failed tests, so we can assume this. */
            expected == null ? "<null>" : expected.toString(),
            received == null ? "<null>" : received.toString()
        );
        if(expected == null && received != null) Assert.fail();
        if(expected != null && !expected.equals(received)) Assert.fail();
    }

}
