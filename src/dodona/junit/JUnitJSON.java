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
        launcher.registerTestExecutionListeners(new JSONListener());
        launcher.execute(request);
    }
}
