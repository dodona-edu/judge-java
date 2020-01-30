package dodona.junit;

import java.util.Optional;
import java.security.Permission;
import static java.lang.Thread.currentThread;

import org.junit.runner.JUnitCore;

import dodona.feedback.Message;
import dodona.feedback.AppendMessage;
import dodona.json.Json;

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

        NoExitSecurityManager sm = new NoExitSecurityManager(System.getSecurityManager());
        System.setSecurityManager(sm);
        JUnitCore core = new JUnitCore();
        core.addListener(new JSONListener());
        core.run(new Class<?>[]{ testSuite });
        System.setSecurityManager(sm.getPrevious());
    }

    private static class NoExitSecurityManager extends SecurityManager {
        private Optional<SecurityManager> previous;

        public NoExitSecurityManager(SecurityManager previous) {
            this.previous = Optional.ofNullable(previous);
        }

        @Override public void checkPermission(Permission perm) {
            previous.ifPresent(sm -> sm.checkPermission(perm));
        }

        @Override public void checkPermission(Permission perm, Object context) {
            previous.ifPresent(sm -> sm.checkPermission(perm, context));
        }

        @Override public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }

        public SecurityManager getPrevious() {
            return previous.orElse(null);
        }
    }

}
