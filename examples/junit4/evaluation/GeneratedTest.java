import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dodona.junit.TabTitle;

/**
 * A more complex test class.
 *
 * Runs a larger number of generated tests.
 */
@RunWith(Parameterized.class)
@TabTitle("Random Tests") // Giving a name other than 'Test' to the tab in Dodona.
public class GeneratedTest {

    public static final int TEST_COUNT = 50;
    public static final long SEED = 42; // Fixed random seed to get the same tests for every student.
    public static final int TEST_MAX = 10000;

    /** This method generates a number of tests. */

    @Parameterized.Parameters(name = "{index} | addOne({0}) == {1}")
    public static Iterable<Object[]> data() {
        List<Object[]> data = new ArrayList<>();
        Random random = new Random(SEED);
        for(int i = 0; i < TEST_COUNT; i++) {
            int testNumber = random.nextInt(TEST_MAX);
            data.add(new Object[] { testNumber, testNumber + 1 });
        }
        return data;
    }

    /* Each test, in order, is filled in here and run. */
    @Parameterized.Parameter(value=0) public int testNumber;
    @Parameterized.Parameter(value=1) public int expected;

    private static SimpleInterface studentSolution;

    @Test
    public void test() {
        if(studentSolution == null) studentSolution = new Simple();
        Assert.assertEquals(expected, studentSolution.addOne(testNumber));
    }

}
