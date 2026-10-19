import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;

import dodona.junit.ExpectedSystemExit;
import dodona.junit.TabTitle;
import dodona.junit.TestDescription;
import dodona.reflection.AssertionStubber;

@TabTitle("Calculator")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CalculatorTest {

    @RegisterExtension
    public ExpectedSystemExit exit = ExpectedSystemExit.none();

    private CalculatorInterface calculator() {
        return new AssertionStubber().stub(CalculatorInterface.class, Calculator.class);
    }

    @Test
    @TestDescription("square(3) exits with status 3")
    public void test1Expected() {
        exit.expectSystemExitWithStatus(3);
        calculator().square(3);
    }

    @Test
    @TestDescription("square(3) without an expectation")
    public void test2Unexpected() {
        calculator().square(3);
    }

    @Test
    @TestDescription("cube(3) throws an exception")
    public void test3Exception() {
        calculator().cube(3);
    }

    @Test
    @TestDescription("twice(3) exits")
    public void test4NotCalled() {
        exit.expectSystemExit();
        calculator().twice(3);
    }

}
