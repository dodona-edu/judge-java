package dodona.junit;

import java.util.Optional;
import java.util.Locale;
import java.security.Permission;
import static java.lang.Thread.currentThread;

import org.junit.runner.JUnitCore;

import dodona.feedback.Message;
import dodona.feedback.AppendMessage;
import dodona.json.Json;

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

        Locale.setDefault(Locale.Category.FORMAT, new Locale("en_US_POSIX"));

        JUnitCore core = new JUnitCore();
        core.addListener(new JSONListener());
        core.run(new Class<?>[] { testSuite });
    }

}
