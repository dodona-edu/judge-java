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
            while(thrown != null) {
                if(thrown instanceof AnnotatedThrowable) {
                    write(new StartTestcase(Message.code(failure.getException().toString())));
                    write(new EscalateStatus(Status.WRONG, "Fout"));
                    write(new AppendMessage(((AnnotatedThrowable) thrown).getFeedback()));
                    write(new CloseTestcase(false));
                } else if(thrown instanceof TestCarryingThrowable) {
                    write(new StartTestcase(Message.plain("")));
                    write(((TestCarryingThrowable) thrown).getStartTest());
                    write(((TestCarryingThrowable) thrown).getCloseTest());
                    write(new CloseTestcase(false));
                } else if(!(thrown instanceof AssertionError)) {
                    write(new StartTestcase(Message.code(thrown.toString())));
                    write(new EscalateStatus(Status.RUNTIME_ERROR, "Uitvoeringsfout"));
                    write(new AppendMessage(Message.code("Caused by " + thrown)));

                    StackTraceElement[] stacktrace = thrown.getStackTrace();
                    boolean leftDefaultPackage = false;
                    for(int i = 0; i < stacktrace.length; i++) {
                        // student code in default package
                        boolean inDefaultPackage = stacktrace[i].getClassName().indexOf('.') < 0;
                        if(leftDefaultPackage && !inDefaultPackage) break;
                        if(inDefaultPackage) leftDefaultPackage = true;
                        write(new AppendMessage(Message.code(" at " + stacktrace[i].toString())));
                    }
                    write(new CloseTestcase(false));
                }
                thrown = thrown.getCause();
            }

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
