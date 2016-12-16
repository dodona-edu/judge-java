package dodona.junit;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.Result;

public class TestCounter extends RunListener {

    private static final UnaryOperator<Integer> INCREMENT = new UnaryOperator<Integer>() {
        public Integer apply(Integer value) { return value + 1; }
    };

    private final ArrayList<Integer> tested;
    private final ArrayList<Integer> failed;

    private Integer lastTested;
    private Integer lastFailed;

    public TestCounter() {
        this.tested = new ArrayList<>();
        this.failed = new ArrayList<>();
    }

    public void testSuiteStarted(Description description) throws Exception {
        this.tested.add(0);
        this.failed.add(0);
    }

    public void testSuiteFinished(Description description) throws Exception {
        this.lastTested = this.tested.remove(this.tested.size() - 1);
        this.lastFailed = this.failed.remove(this.failed.size() - 1);
    }

    public void testFinished(Description description) throws Exception {
        this.tested.replaceAll(INCREMENT);
    }

    public void testFailure(Failure failure) throws Exception {
        this.failed.replaceAll(INCREMENT);
    }

    public Integer getLastTested() { return lastTested; }
    public Integer getLastFailed() { return lastFailed; }

}
