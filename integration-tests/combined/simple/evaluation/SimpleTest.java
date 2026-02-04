import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SimpleTest {

    private SimpleInterface solution;

    @Test
    public void testZero() {
        if (solution == null)
            solution = new Simple();
        Assertions.assertEquals(1, solution.addOne(0));
    }

    @Test
    public void testOne() {
        if (solution == null)
            solution = new Simple();
        Assertions.assertEquals(2, solution.addOne(1));
    }

}
