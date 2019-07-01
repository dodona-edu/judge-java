package dodona.junit;

import dodona.feedback.Status;
import dodona.feedback.StartTab;
import dodona.feedback.StartContext;
import dodona.feedback.StartTestcase;
import dodona.feedback.CloseTab;
import dodona.feedback.CloseContext;
import dodona.feedback.CloseTestcase;
import dodona.feedback.Message;
import dodona.feedback.AppendMessage;
import dodona.feedback.EscalateStatus;
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
    private final Json json;

    public JSONListener() {
        this(System.out);
        System.setOut(new PrintStream(new IllegalOutputStream()));
    }

    public JSONListener(PrintStream writer) {
        this.writer = writer;
        this.json = new Json();
    }

    private void write(Object src) {
        writer.print(json.asString(src));
    }

    /* COMPLETE RUN */
    public void beforeExecution() {}

    public void afterExecution() {}

    public void beforeTab(Description description) {
        TabTitle tabAnnotation = description.getAnnotation(TabTitle.class);
        String title = tabAnnotation == null ? "Test" : tabAnnotation.value();
        write(new StartTab(title));
    }

    public void afterTab() {
        write(new CloseTab());
    }

    public void beforeTest(Description description) {
        write(new StartContext(Message.code(description.getDisplayName())));
    }

    public void aftertest(Failure failure) {
        if(failure == null) {
            write(new CloseContext(true));
        } else {
            Throwable thrown = failure.getException();
            Message feedback = null;
            if(thrown instanceof AnnotatedThrowable) {
                feedback = ((AnnotatedThrowable) thrown).getFeedback();
                thrown = thrown.getCause();
            }

            if(thrown instanceof TestCarryingThrowable) {
                write(new StartTestcase(Message.plain("")));
                write(((TestCarryingThrowable) thrown).getStartTest());
                write(((TestCarryingThrowable) thrown).getCloseTest());
            } else if(thrown instanceof AssertionError) {
                write(new EscalateStatus(Status.WRONG, "Fout"));
                write(new StartTestcase(Message.code(thrown.getMessage() == null ? "" : thrown.getMessage())));
            } else {
                Throwable deepest = thrown;
                while(deepest.getCause() != null) deepest = deepest.getCause();
                write(new StartTestcase(Message.code(deepest.toString())));
                write(new EscalateStatus(Status.RUNTIME_ERROR, "Uitvoeringsfout"));
                while(thrown != null) {
                    StringBuilder message = new StringBuilder();
                    message.append("Caused by " + thrown);
                    StackTraceElement[] stacktrace = thrown.getStackTrace();
                    boolean leftDefaultPackage = false;
                    for(int i = 0; i < stacktrace.length; i++) {
                        // student code in default package
                        boolean inDefaultPackage = stacktrace[i].getClassName().indexOf('.') < 0;
                        if(leftDefaultPackage && !inDefaultPackage) break;
                        if(inDefaultPackage) leftDefaultPackage = true;
                        message.append("\n at " + stacktrace[i].toString());
                    }
                    write(new AppendMessage(Message.code(message.toString())));
                    thrown = thrown.getCause();
                }
            }

            if(feedback != null) write(new AppendMessage(feedback));
            write(new CloseTestcase(false));
            write(new CloseContext(false));
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

        write(new AppendMessage(Message.internalError(stackCollector.toString())));
    }

    public void testIgnored(Description description) throws Exception {}

}
