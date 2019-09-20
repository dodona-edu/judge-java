package dodona.junit;

import dodona.feedback.Message;
import dodona.feedback.AppendMessage;
import dodona.json.Json;

import org.junit.runner.JUnitCore;

import static java.lang.Thread.currentThread;

public class JUnitJSON {
    public static final String PROPERTY_LANGUAGE = "dodona.language";

    public static void main(String... args) {
        Class<?> testSuite = null;
        try {
            testSuite = Class.forName("TestSuite", true, currentThread().getContextClassLoader());
        } catch(ClassNotFoundException e) {
            System.out.println(new Json().asString(new AppendMessage(Message.internalError("TestSuite class not found."))));
            System.exit(1);
        }

        JUnitCore core = new JUnitCore();
        core.addListener(new JSONListener());
        core.run(new Class<?>[]{ testSuite });
    }

}
