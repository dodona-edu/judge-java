package dodona.junit;

import dodona.feedback.AppendMessage;
import dodona.feedback.CloseContext;
import dodona.feedback.CloseTab;
import dodona.feedback.CloseTestcase;
import dodona.feedback.EscalateStatus;
import dodona.feedback.Message;
import dodona.feedback.Permission;
import dodona.feedback.StartContext;
import dodona.feedback.StartTab;
import dodona.feedback.StartTestcase;
import dodona.feedback.Status;
import dodona.json.Json;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.runners.model.TestTimedOutException;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JSONListener implements TestExecutionListener {
    private static final int STACKSIZE = 50;

    private final PrintStream writer;
    private final Json json;
    private TestPlan testPlan;

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

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        this.testPlan = testPlan;
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        this.testPlan = null;
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (isTab(testIdentifier)) {
            beforeTab(testIdentifier);
        } else if (testIdentifier.isTest()) {
            beforeTest(testIdentifier);
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (isTab(testIdentifier)) {
            afterTab();
        } else if (testIdentifier.isTest()) {
            afterTest(testExecutionResult);
        }
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    }

    public void beforeTab(TestIdentifier testIdentifier) {
        final String title = getTabTitle(testIdentifier);
        final Permission permission = getTabPermission(testIdentifier)
                .orElse(TabPermission.DEFAULT);
        write(new StartTab(title, permission));
    }

    public void afterTab() {
        write(new CloseTab());
    }

    public void beforeTest(TestIdentifier testIdentifier) {
        final String title = getDescription(testIdentifier);
        write(new StartContext(Message.code(title)));
    }

    public void afterTest(TestExecutionResult result) {
        if (result.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
            write(new CloseContext(true));
        } else {
            handleTestFailure(result);
        }
    }

    private void handleTestFailure(TestExecutionResult result) {
        Throwable thrown = result.getThrowable().orElse(null);
        List<Message> feedback = new ArrayList<>();

        if (thrown instanceof AnnotatedThrowable at) {
            feedback = at.getFeedback();
            thrown = thrown.getCause();
        }

        switch (thrown) {
            case TestCarryingThrowable tct -> handleTestCarryingThrowable(tct);
            case AssertionError ae -> handleAssertionError(ae);
            case Throwable t -> handleException(t);
            case null -> handleUnknownError();
        }

        feedback.stream().map(AppendMessage::new).forEach(this::write);
        write(new CloseTestcase(false));
        write(new CloseContext(false));
    }

    private void handleTestCarryingThrowable(TestCarryingThrowable thrown) {
        write(new StartTestcase(Message.plain("")));
        write(thrown.getStartTest());
        thrown.getMessages().stream().map(AppendMessage::new).forEach(this::write);
        write(thrown.getCloseTest());
    }

    private void handleAssertionError(AssertionError thrown) {
        write(new EscalateStatus(Status.WRONG, "Wrong"));
        write(new StartTestcase(Message.code(thrown.getMessage() == null ? "" : thrown.getMessage())));
    }

    private void handleException(Throwable thrown) {
        Throwable deepest = thrown;
        while (deepest.getCause() != null) {
            deepest = deepest.getCause();
        }
        write(new StartTestcase(Message.code(deepest.toString())));

        if (thrown instanceof TestTimedOutException) {
            write(new EscalateStatus(Status.TIME_LIMIT_EXCEEDED, "Time limit exceeded"));
        } else {
            write(new EscalateStatus(Status.RUNTIME_ERROR, "Runtime error"));
        }

        while (thrown != null) {
            StringBuilder message = new StringBuilder();
            message.append("Caused by " + thrown);
            StackTraceElement[] stacktrace = thrown.getStackTrace();
            boolean leftDefaultPackage = false;
            for (int i = 0; i < stacktrace.length && i < STACKSIZE; i++) {
                boolean inDefaultPackage = stacktrace[i].getClassName().indexOf('.') < 0;
                if (leftDefaultPackage && !inDefaultPackage) {
                    break;
                }
                if (inDefaultPackage) {
                    leftDefaultPackage = true;
                }
                message.append("\n at " + stacktrace[i].toString());
            }
            if (stacktrace.length >= STACKSIZE) {
                message.append("\n ...");
            }
            write(new AppendMessage(Message.code(message.toString())));
            thrown = thrown.getCause();
        }
    }

    private void handleUnknownError() {
        write(new EscalateStatus(Status.RUNTIME_ERROR, "Unknown Error"));
        write(new StartTestcase(Message.plain("Unknown Error")));
    }

    // Helper to determine if an ID is a "Tab" (Test Class)
    private boolean isTab(TestIdentifier testIdentifier) {
        if (!testIdentifier.isContainer() || testPlan == null || testIdentifier.getSource().isEmpty()) {
            return false;
        }

        String parent = testIdentifier.getParentId().orElse("");
        if (!parent.contains("TestSuite")) {
            return false;
        }

        // Identify tabs more robustly by checking the uniqueId segment type:
        // - regular JUnit 5 classes use "[class:...]"
        // - JUnit 4 (vintage) runners use "[runner:...]"
        // Suites use "[suite:...]" and must NOT be tabs.
        String uid = testIdentifier.getUniqueId();
        return (uid.contains("[class:") || uid.contains("[runner:"));
    }

    private String getDescription(TestIdentifier testIdentifier) {
        return getAnnotation(testIdentifier, TestDescription.class)
                .map(TestDescription::value)
                .orElse(testIdentifier.getDisplayName());
    }

    private String getTabTitle(TestIdentifier testIdentifier) {
        return getAnnotation(testIdentifier, TabTitle.class)
                .map(TabTitle::value)
                .orElse(testIdentifier.getDisplayName());
    }

    private Optional<Permission> getTabPermission(TestIdentifier testIdentifier) {
        return getAnnotation(testIdentifier, TabPermission.class).map(TabPermission::value);
    }

    private <A extends Annotation> Optional<A> getAnnotation(TestIdentifier testIdentifier, Class<A> annotationClass) {
        return testIdentifier.getSource().flatMap(source -> switch (source) {
            case ClassSource cs -> Optional.ofNullable(cs.getJavaClass().getAnnotation(annotationClass));
            case MethodSource ms -> Optional.ofNullable(ms.getJavaMethod().getAnnotation(annotationClass));
            default -> Optional.empty();
        });
    }
}
