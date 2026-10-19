package dodona.junit;

import dodona.feedback.AppendMessage;
import dodona.feedback.Message;
import dodona.json.Json;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.util.Locale;
import static java.lang.Thread.currentThread;

public class JUnitJSON {
    public static final String PROPERTY_OUTPUT_CUTOFF = "dodona.output_cutoff";

    private static byte[] reserve = new byte[1 << 20];

    public static void main(String... args) {
        Class<?> testSuite = null;
        try {
            testSuite = Class.forName("TestSuite", true, currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            System.out.println(
                    new Json().asString(new AppendMessage(Message.internalError("TestSuite class not found."))));
            System.exit(1);
        }

        Locale.setDefault(Locale.Category.FORMAT, Locale.forLanguageTag("en_US_POSIX"));

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testSuite))
                .build();

        Launcher launcher = LauncherFactory.create();
        JSONListener listener = new JSONListener();
        launcher.registerTestExecutionListeners(listener);
        try {
            launcher.execute(request);
        } catch (Throwable t) {
            // Free room to write the feedback when the student's code still holds the memory
            reserve = null;
            // JUnit's cleanup can wrap the OutOfMemoryError in an IllegalArgumentException (self-suppression)
            if (!(t instanceof OutOfMemoryError) && !(t.getCause() instanceof OutOfMemoryError)) {
                throw t;
            }
            // JUnit Jupiter rethrows OutOfMemoryError as unrecoverable instead of reporting the test as failed
            listener.abortWithOutOfMemory();
            // Exit explicitly: JUnit's timeout thread is not a daemon and would keep the JVM alive
            System.exit(0);
        }
    }
}
