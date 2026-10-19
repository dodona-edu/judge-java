import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;

import dodona.junit.ExpectedSystemExit;
import dodona.junit.TabTitle;
import dodona.junit.TestDescription;

@TabTitle("Calculator")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CalculatorTest {

    @RegisterExtension
    public ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    @TestDescription("square(3) exits with status 3")
    public void test1Status() {
        exit.expectSystemExitWithStatus(3);
        new Calculator().square(3);
    }

    @Test
    @TestDescription("cube(3) exits with status 3")
    public void test2WrongStatus() {
        exit.expectSystemExitWithStatus(3);
        new Calculator().cube(3);
    }

    @Test
    @TestDescription("cube(3) exits")
    public void test3AnyStatus() {
        exit.expectSystemExit();
        new Calculator().cube(3);
    }

    @Test
    @TestDescription("twice(3) exits")
    public void test4NotCalled() {
        exit.expectSystemExit();
        new Calculator().twice(3);
    }

    @Test
    @TestDescription("square(3) without an expectation")
    public void test5Unexpected() {
        new Calculator().square(3);
    }

}
