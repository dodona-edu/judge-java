package dodona.junit;

import dodona.feedback.AppendMessage;
import dodona.feedback.CloseContext;
import dodona.feedback.CloseTab;
import dodona.feedback.CloseTestcase;
import dodona.feedback.EscalateStatus;
import dodona.feedback.Message;
import dodona.feedback.StartContext;
import dodona.feedback.StartTab;
import dodona.feedback.StartTestcase;
import dodona.feedback.Status;
import dodona.i18n.I18nTestDescription;
import dodona.i18n.Language;
import dodona.json.Json;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class JSONListener extends RunListener {
    private static final int STACKSIZE = 50;

    private final ResourceBundle descriptions;
    private final PrintStream writer;
    private final Json json;

    public JSONListener() {
        this(System.out);
        System.setOut(new PrintStream(new IllegalOutputStream()));
    }

    public JSONListener(PrintStream writer) {
        this.descriptions = getBundleIfExists("descriptions", Language.current());
        this.writer = writer;
        this.json = new Json();
    }

    /**
     * Gets the given resource bundle for the current language, if it exists.
     *
     * @param base the base name of the resource bundle
     * @return the bundle if it exists, or null otherwise
     */
    private static ResourceBundle getBundleIfExists(final String base, final Language language) {
        try {
            final String bundleName = String.format("%s.%s.properties", base, language.getIdentifier());
            final InputStream bundleStream = JSONListener.class.getClassLoader().getResourceAsStream(bundleName);
            return new PropertyResourceBundle(bundleStream);
        } catch (final Exception exception) {
            return null;
        }
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
        final String title = this.getDescription(description);
        write(new StartContext(Message.code(title)));
    }

    public void aftertest(Failure failure) {
        if(failure == null) {
            write(new CloseContext(true));
        } else {
            Throwable thrown = failure.getException();
            List<Message> feedback = new ArrayList<>();
            if(thrown instanceof AnnotatedThrowable) {
                feedback = ((AnnotatedThrowable) thrown).getFeedback();
                thrown = thrown.getCause();
            }

            if(thrown instanceof TestCarryingThrowable) {
                write(new StartTestcase(Message.plain("")));
                write(((TestCarryingThrowable) thrown).getStartTest());
                ((TestCarryingThrowable) thrown).getMessages().stream().map(AppendMessage::new).forEach(this::write);
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
                    for(int i = 0; i < stacktrace.length && i < STACKSIZE; i++) {
                        // student code in default package
                        boolean inDefaultPackage = stacktrace[i].getClassName().indexOf('.') < 0;
                        if(leftDefaultPackage && !inDefaultPackage) break;
                        if(inDefaultPackage) leftDefaultPackage = true;
                        message.append("\n at " + stacktrace[i].toString());
                    }
                    if(stacktrace.length >= STACKSIZE) message.append("\n ...");
                    write(new AppendMessage(Message.code(message.toString())));
                    thrown = thrown.getCause();
                }
            }

            feedback.stream().map(AppendMessage::new).forEach(this::write);
            write(new CloseTestcase(false));
            write(new CloseContext(false));
        }
    }

    /**
     * Get the human-friendly version of the test name.
     *
     * @param desc the description
     * @return the human-friendly version
     */
    private String getDescription(final Description desc) {
        return getI18nTestDescription(desc)
            .orElseGet(() -> getTestDescription(desc)
                .orElse(desc.getDisplayName()));
    }

    /**
     * Parse a @I18nTestDescription annotation.
     *
     * @param desc the description
     * @return the value of the I18nTestDescription annotation if available
     */
    private Optional<String> getI18nTestDescription(final Description desc) {
        return Optional.ofNullable(this.descriptions).flatMap(bundle ->
            Optional.ofNullable(desc.getAnnotation(I18nTestDescription.class))
                .map(I18nTestDescription::value)
                .filter(bundle::containsKey)
                .map(bundle::getString)
        );
    }

    /**
     * Parse a @TestDescription annotation.
     *
     * @param desc the description
     * @return the value of the TestDescription annotation if available
     */
    private static Optional<String> getTestDescription(final Description desc) {
        return Optional
            .ofNullable(desc.getAnnotation(TestDescription.class))
            .map(TestDescription::value);
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
