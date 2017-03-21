package dodona.junit;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.Result;

import dodona.json.Json;
import dodona.feedback.Feedback;
import dodona.feedback.Tab;
import dodona.feedback.Message;
import dodona.feedback.Context;
import dodona.feedback.Testcase;
import dodona.feedback.Status;

public class JSONListener extends RunListener {

    private final PrintStream writer;
    private final Feedback feedback;
    private final TestCounter counter;

    /** Stores the current tab, if any. */
    private int depth;
    private Tab currentTab;

    public JSONListener(TestCounter counter) {
        this(System.out, counter);
        System.setOut(new PrintStream(new IllegalOutputStream()));
    }

    public JSONListener(PrintStream writer, TestCounter counter) {
        this.writer = writer;
        this.feedback = new Feedback();
        this.counter = counter;
        this.depth = 0;
        this.currentTab = null;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Json json = new Json();
            writer.print(json.asString(feedback));
        }));
    }

    /**
     * Called before any tests have been run. This may be called on an
     * arbitrary thread.
     *
     * @param description describes the tests to be run
     */
    public void testRunStarted(Description description) throws Exception {
    }

    /**
     * Called when all tests have finished. This may be called on an
     * arbitrary thread.
     *
     * @param result the summary of the test run, including all the tests that failed
     */
    public void testRunFinished(Result result) throws Exception {
    }

    /**
     * Called when a test suite is about to be started. If this method is
     * called for a given {@link Description}, then {@link #testSuiteFinished(Description)}
     * will also be called for the same {@code Description}.
     *
     * <p>Note that not all runners will call this method, so runners should
     * be prepared to handle {@link #testStarted(Description)} calls for tests
     * where there was no cooresponding {@code testSuiteStarted()} call for
     * the parent {@code Description}.
     *
     * @param description the description of the test suite that is about to be run
     *                    (generally a class name)
     * @since 4.13
     */
    public void testSuiteStarted(Description description) throws Exception {
        if(depth++ != 2) return;
        currentTab = new Tab();

        TabTitle tabAnnotation = description.getAnnotation(TabTitle.class);
        String title = tabAnnotation == null ? null : tabAnnotation.value();
        currentTab.setTitle(title);
    }

    /**
     * Called when a test suite has finished, whether the test suite succeeds or fails.
     * This method will not be called for a given {@link Description} unless
     * {@link #testSuiteStarted(Description)} was called for the same @code Description}.
     *
     * @param description the description of the test suite that just ran
     * @since 4.13
     */
    public void testSuiteFinished(Description description) throws Exception {
        if(--depth != 2) return;
        if(currentTab != null) {
            if(counter.getLastFailed() != null && counter.getLastFailed() < counter.getLastTested()) {
                Context summary = new Context();
                summary.setAccepted(true);
                summary.setDescription(Message.plain(Integer.toString(counter.getLastTested() - counter.getLastFailed()) + " succesful tests not shown."));
                currentTab.prependChild(summary);
            }
            feedback.addChild(currentTab);
            currentTab = null;
        }
    }

    /**
     * Called when an atomic test is about to be started.
     *
     * @param description the description of the test that is about to be run
     * (generally a class and method name)
     */
    public void testStarted(Description description) throws Exception {
    }

    /**
     * Called when an atomic test has finished, whether the test succeeds or fails.
     *
     * @param description the description of the test that just ran
     */
    public void testFinished(Description description) throws Exception {
    }

    /**
     * Called when an atomic test fails, or when a listener throws an exception.
     *
     * <p>In the case of a failure of an atomic test, this method will be called
     * with the same {@code Description} passed to
     * {@link #testStarted(Description)}, from the same thread that called
     * {@link #testStarted(Description)}.
     *
     * <p>In the case of a listener throwing an exception, this will be called with
     * a {@code Description} of {@link Description#TEST_MECHANISM}, and may be called
     * on an arbitrary thread.
     *
     * @param failure describes the test that failed and the exception that was thrown
     */
    public void testFailure(Failure failure) throws Exception {
        if(currentTab == null) {
            feedback.addMessage(Message.internalError(
                "Incorrect wrapping of all testclasses in a single testsuite."
            ));
            return;
        }

        Context context = new Context();
        context.setDescription(Message.code(failure.getTestHeader()));
        currentTab.addChild(context);
        currentTab.incrementBadgeCount();

        Testcase testcase = new Testcase();
        testcase.setDescription(Message.code(failure.getException().toString()));
        context.addChild(testcase);

        feedback.setAccepted(false);
        feedback.worseStatus(Status.WRONG);
        Throwable thrown = failure.getException();
        while(thrown != null) {
            if(thrown instanceof AnnotatedThrowable) {
                testcase.addMessage(((AnnotatedThrowable) thrown).getFeedback());
            } else if(!(thrown instanceof AssertionError)) {
                feedback.worseStatus(Status.RUNTIME_ERROR);
                StackTraceElement[] stacktrace = thrown.getStackTrace();
                for(int i = 0; i < stacktrace.length; i++) {
                    // student code in default package
                    if(stacktrace[i].getClassName().indexOf('.') > 0) break;
                    testcase.addMessage(Message.code("at " + stacktrace[i].toString()));
                }
            }
            thrown = thrown.getCause();
        }
    }

    /**
     * Called when an atomic test flags that it assumes a condition that is
     * false
     *
     * @param failure describes the test that failed and the
     * {@link org.junit.AssumptionViolatedException} that was thrown
     */
    public void testAssumptionFailure(Failure failure) {
        StringWriter stackCollector = new StringWriter();
        stackCollector.append("testAssumptionFailure in " +
                              failure.getTestHeader() + ": " +
                              failure.getException().getMessage() + "\n");
        failure.getException().printStackTrace(new PrintWriter(stackCollector));

        feedback.addMessage(Message.internalError(stackCollector.toString()));
    }

    /**
     * Called when a test will not be run, generally because a test method is annotated
     * with {@link org.junit.Ignore}.
     *
     * @param description describes the test that will not be run
     */
    public void testIgnored(Description description) throws Exception {
    }

}
