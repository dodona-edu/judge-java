import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dodona.junit.TabTitle;
import dodona.junit.TestDescription;

@TabTitle("Calculator")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CalculatorTest {

    @Test
    @TestDescription("square(3)")
    public void test1Square() {
        Assertions.assertEquals(9, new Calculator().square(3));
    }

    @Test
    @TestDescription("twice(3)")
    public void test2Twice() {
        Assertions.assertEquals(6, new Calculator().twice(3));
    }

}
