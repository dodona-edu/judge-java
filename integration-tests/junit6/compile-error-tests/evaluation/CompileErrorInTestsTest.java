import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompileErrorInTestsTest {
    @Test
    public void testMethod() {
        // Intentional typo to trigger a compilation error in the tests.
        Assertions.asserEquals(true, true);
    }
}
