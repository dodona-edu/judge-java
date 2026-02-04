import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A more complex test class.
 *
 * Runs a larger number of generated tests.
 */
@DisplayName("Random Tests") // Giving a name other than 'Test' to the tab in Dodona.
@ParameterizedClass
@MethodSource("data")
public class GeneratedTest {

    public static final int TEST_COUNT = 2;
    public static final long SEED = 42; // Fixed random seed to get the same tests for every student.
    public static final int TEST_MAX = 100;


    @Parameter(0)
    int testNumber;
    @Parameter(1)
    int expected;

    /** This method generates a number of tests. */

    public static Iterable<Object[]> data() {
        List<Object[]> data = new ArrayList<>();
        Random random = new Random(SEED);
        for(int i = 0; i < TEST_COUNT; i++) {
            int testNumber = random.nextInt(TEST_MAX);
            data.add(new Object[] { testNumber, testNumber + 1 });
        }
        return data;
    }

    private static SimpleInterface studentSolution;

    @Test
    public void test() {
        if(studentSolution == null) studentSolution = new Simple();
        Assertions.assertEquals(expected, studentSolution.addOne(testNumber));
    }

}
