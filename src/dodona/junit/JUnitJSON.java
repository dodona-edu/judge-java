package dodona.junit;

import dodona.feedback.Feedback;
import dodona.feedback.Message;
import dodona.json.Json;

import org.junit.runner.JUnitCore;

import static java.lang.Thread.currentThread;

public class JUnitJSON {

    public static void main(String... args) {
        Class<?> testSuite = null;
        try {
            testSuite = Class.forName("TestSuite", true, currentThread().getContextClassLoader());
        } catch(ClassNotFoundException e) {
            Json json = new Json();
            Feedback feedback = new Feedback();
            feedback.addMessage(Message.internalError("TestSuite class not found."));
            System.out.println(json.asString(feedback));
            System.exit(1);
        }

        JUnitCore core = new JUnitCore();
        core.addListener(new JSONListener());
        core.run(new Class<?>[]{ testSuite });
    }

}
