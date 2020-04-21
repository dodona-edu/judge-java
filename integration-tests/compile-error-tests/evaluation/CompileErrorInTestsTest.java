import org.junit.Assert;
import org.junit.Test;

public class CompileErrorInTestsTest {
    @Test
    public void testMethod() {
        // Intentional typo to trigger a compilation error in the tests.
        Assert.asserEquals(true, true);
    }
}
