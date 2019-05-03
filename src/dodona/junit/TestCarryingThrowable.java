package dodona.junit;

import dodona.feedback.StartTest;
import dodona.feedback.CloseTest;

public class TestCarryingThrowable extends Throwable {
    private static final long serialVersionUID = 1L;

    private StartTest startTest;
    private CloseTest closeTest;

    public TestCarryingThrowable(Throwable cause, StartTest startTest, CloseTest closeTest) {
        super(cause);
        this.startTest = startTest;
        this.closeTest = closeTest;
    }

    public StartTest getStartTest() {
        return startTest;
    }

    public CloseTest getCloseTest() {
        return closeTest;
    }

}
