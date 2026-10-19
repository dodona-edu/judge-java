package dodona.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;

/**
 * Replacement for system-rules' ExpectedSystemExit, which needs a SecurityManager.
 * Works as a JUnit 4 rule and as a Jupiter extension.
 */
public class ExpectedSystemExit implements TestRule, InvocationInterceptor {

    private boolean expectExit;
    private Integer expectedStatus;

    private ExpectedSystemExit() {
    }

    public static ExpectedSystemExit none() {
        return new ExpectedSystemExit();
    }

    public void expectSystemExit() {
        expectExit = true;
    }

    public void expectSystemExitWithStatus(int status) {
        expectExit = true;
        expectedStatus = status;
    }

    private interface Body {
        void run() throws Throwable;
    }

    private void evaluate(Body body) throws Throwable {
        try {
            body.run();
            if (expectExit) {
                throw new AssertionError("System.exit has not been called.");
            }
        } catch (SystemExitError e) {
            if (!expectExit) {
                throw e;
            }
            if (expectedStatus != null && expectedStatus != e.getStatus()) {
                throw new AssertionError(
                        "expected System.exit(" + expectedStatus + ") but was System.exit(" + e.getStatus() + ")");
            }
        } finally {
            expectExit = false;
            expectedStatus = null;
        }
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                ExpectedSystemExit.this.evaluate(base::evaluate);
            }
        };
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        evaluate(invocation::proceed);
    }

    @Override
    public void interceptTestTemplateMethod(Invocation<Void> invocation,
                                            ReflectiveInvocationContext<Method> invocationContext,
                                            ExtensionContext extensionContext) throws Throwable {
        evaluate(invocation::proceed);
    }
}
