package dodona;

import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Marks a test of a known bug: the test asserts the intended behaviour and
 * passes only while that assertion still fails, so fixing the bug makes it fail
 * as a reminder to assert the behaviour directly.
 */
public final class KnownBug {

    private KnownBug() {
    }

    public static void expectFailure(String bug, Executable intended) {
        assertThrows(AssertionFailedError.class, intended,
                "Known bug fixed, assert the intended behaviour directly: " + bug);
    }
}
