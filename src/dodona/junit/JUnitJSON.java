package dodona.junit;

import dodona.feedback.AppendMessage;
import dodona.feedback.Message;
import dodona.json.Json;
import dodona.vintage.JSONListener;
import org.junit.runner.JUnitCore;

import java.util.Locale;

import static java.lang.Thread.currentThread;

public class JUnitJSON {
    public static final String PROPERTY_LANGUAGE = "dodona.language";
    public static final String PROPERTY_OUTPUT_CUTOFF = "dodona.output_cutoff";

    public static void main(String... args) {
        Class<?> testSuite = null;
        try {
            testSuite = Class.forName("TestSuite", true, currentThread().getContextClassLoader());
        } catch(ClassNotFoundException e) {
            System.out.println(new Json().asString(new AppendMessage(Message.internalError("TestSuite class not found."))));
            System.exit(1);
        }

        Locale.setDefault(Locale.Category.FORMAT, Locale.forLanguageTag("en_US_POSIX"));

        JUnitCore core = new JUnitCore();
        core.addListener(new JSONListener());
        core.run(new Class<?>[]{ testSuite });
    }
}
