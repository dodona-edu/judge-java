package dodona.junit;

import dodona.feedback.Test;

public class TestCarryingThrowable extends Throwable {
    private static final long serialVersionUID = 1L;

    private Test test;

    public TestCarryingThrowable(Throwable cause, Test test) {
        super(cause);
        this.test = test;
    }

    public Test getTest() {
        return test;
    }

}
