package dodona.junit;

import dodona.feedback.Message;
import dodona.feedback.Status;
import dodona.feedback.StatusPair;
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
        return base;
    }

    public void compare(Object expected, Object received) {
        compare(null, expected, received);
    }

    public void compare(String description, Object expected, Object received) {
        TestCarryingThrowable test = createTest(description, expected, received);
        if(expected == null && received != null) throw test;
        if(expected != null && !expected.equals(received)) throw test;
    }

    public void compare(final Double expected,
                        final Double received,
                        final double delta) {
        compare(null, expected, received, delta);
    }

    public void compare(final String description,
                        final Double expected,
                        final Double received,
                        final double delta) {
        TestCarryingThrowable test = createTest(description, expected, received);
        if (expected == null) {
            if (received != null) throw test;
        } else if (received == null || Math.abs(expected - received) > delta) {
           throw test;
        }
    }

    private TestCarryingThrowable createTest(String description, Object expected, Object received) {
        return new TestCarryingThrowable(
            new StartTest(
                expected == null ? "<null>" : expected.toString(),
                description == null ? null : Message.code(description)),
            new CloseTest(
                received == null ? "<null>" : received.toString(),
                new StatusPair(Status.WRONG, null), /* crashed tests won't even get this far */
                false /* This is only visible for failed tests, so we can assume this. */
            ));
    }

}
