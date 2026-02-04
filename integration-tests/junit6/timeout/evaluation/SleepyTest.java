import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SleepyTest {

    @Test
    @org.junit.jupiter.api.Timeout(value = 1, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
    public void test() throws Exception {
        new Sleepy().sleep();
        fail();
    }

}
