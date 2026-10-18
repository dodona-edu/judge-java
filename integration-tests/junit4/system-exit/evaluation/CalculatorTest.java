import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import dodona.junit.TabTitle;
import dodona.junit.TestDescription;

@TabTitle("Calculator")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CalculatorTest {

    @Test
    @TestDescription("square(3)")
    public void test1Square() {
        Assert.assertEquals(9, new Calculator().square(3));
    }

    @Test
    @TestDescription("twice(3)")
    public void test2Twice() {
        Assert.assertEquals(6, new Calculator().twice(3));
    }

}
