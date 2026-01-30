import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompileErrorStudentTest {
    @Test
    public void testMethod() {
        Assertions.assertEquals(true, new CompileErrorStudent().method());
    }
}
