package dodona.junit;

import static java.lang.Thread.currentThread;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.Computer;
import org.junit.runner.notification.RunListener;

import dodona.feedback.Feedback;
import dodona.feedback.Message;
import dodona.json.Json;

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
