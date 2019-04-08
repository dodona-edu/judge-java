package dodona.junit;

import dodona.feedback.Context;
import dodona.feedback.Feedback;
import dodona.feedback.Message;
import dodona.feedback.Status;
import dodona.feedback.Tab;
import dodona.feedback.Testcase;
import dodona.json.Json;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.Result;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class JSONListener extends RunListener {

    private final PrintStream writer;
    private final Feedback feedback;

    public JSONListener() {
        this(System.out);
        System.setOut(new PrintStream(new IllegalOutputStream()));
    }

    public JSONListener(PrintStream writer) {
        this.writer = writer;
        this.feedback = new Feedback();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            feedback.deriveDescription();
            Json json = new Json();
            writer.print(json.asString(feedback));
            Runtime.getRuntime().halt(0);
        }));
    }

    /* COMPLETE RUN */
    public void beforeExecution() {
        feedback.setAccepted(false);
        feedback.setStatus(Status.TIME_LIMIT_EXCEEDED);
    }

    public void afterExecution() {
        if(feedback.isStatus(Status.TIME_LIMIT_EXCEEDED)) {
            feedback.setAccepted(true);
            feedback.setStatus(Status.CORRECT);
        }
    }

    public void beforeTab(Description description) {
        Tab tab = new Tab();
        feedback.addChild(tab);

        TabTitle tabAnnotation = description.getAnnotation(TabTitle.class);
        String title = tabAnnotation == null ? null : tabAnnotation.value();
        tab.setTitle(title);
    }

    public void afterTab() {}


    public void beforeTest(Description description) {
        Tab tab = feedback.lastChild();
        Context context = new Context();
        context.setDescription(Message.code(description.getDisplayName()));
        context.addMessage(Message.plain("TIMEOUT"));
        tab.addChild(context);
        tab.incrementBadgeCount();
    }

    public void aftertest(Failure failure) {
        Tab tab = feedback.lastChild();
        Context context = tab.lastChild();
        context.clearMessages();
        if(failure == null) {
            tab.decrementBadgeCount();
            context.setAccepted(true);
        } else {
            Testcase testcase = new Testcase();
            context.addChild(testcase);
            testcase.setDescription(Message.code(failure.getException().toString()));

            feedback.setAccepted(false);
            if(feedback.isStatus(Status.TIME_LIMIT_EXCEEDED)) {
                feedback.setStatus(Status.WRONG);
            }
            Throwable thrown = failure.getException();
            while(thrown != null) {
                if(thrown instanceof AnnotatedThrowable) {
                    testcase.addMessage(((AnnotatedThrowable) thrown).getFeedback());
                } else if(thrown instanceof TestCarryingThrowable) {
                    testcase.addChild(((TestCarryingThrowable) thrown).getTest());
                    testcase.setDescription(null);
                } else if(!(thrown instanceof AssertionError)) {
                    feedback.setStatus(Status.RUNTIME_ERROR);
                    testcase.setDescription(Message.code(thrown.toString()));
                    testcase.addMessage(Message.code("Caused by " + thrown));

                    StackTraceElement[] stacktrace = thrown.getStackTrace();
                    boolean leftDefaultPackage = false;
                    for(int i = 0; i < stacktrace.length; i++) {
                        // student code in default package
                        boolean inDefaultPackage = stacktrace[i].getClassName().indexOf('.') < 0;
                        if(leftDefaultPackage && !inDefaultPackage) break;
                        if(inDefaultPackage) leftDefaultPackage = true;
                        testcase.addMessage(Message.code(" at " + stacktrace[i].toString()));
                    }
                }
                thrown = thrown.getCause();
            }
        }
    }

    /* Ugly internals */
    private int depth;

    public void testRunStarted(Description description) throws Exception {
        this.depth = 0;
        beforeExecution();
    }

    public void testRunFinished(Result result) throws Exception {
        afterExecution();
    }

    public void testSuiteStarted(Description description) throws Exception {
        if(depth++ != 2) return;
        beforeTab(description);
    }

    public void testSuiteFinished(Description description) throws Exception {
        if(--depth != 2) return;
        afterTab();
    }

    private Failure currentTestFailure = null;

    public void testStarted(Description description) throws Exception {
        currentTestFailure = null;
        beforeTest(description);
    }

    public void testFinished(Description description) throws Exception {
        aftertest(currentTestFailure);
        currentTestFailure = null;
    }

    public void testFailure(Failure failure) throws Exception {
        currentTestFailure = failure;
    }

    public void testAssumptionFailure(Failure failure) {
        StringWriter stackCollector = new StringWriter();
        stackCollector.append("testAssumptionFailure in " +
                              failure.getTestHeader() + ": " +
                              failure.getException().getMessage() + "\n");
        failure.getException().printStackTrace(new PrintWriter(stackCollector));

        feedback.addMessage(Message.internalError(stackCollector.toString()));
    }

    public void testIgnored(Description description) throws Exception {}

}
