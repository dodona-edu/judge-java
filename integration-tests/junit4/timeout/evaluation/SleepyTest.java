import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SleepyTest {

    @Test(timeout = 1)
    public void test() throws Exception {
        new Sleepy().sleep();
        fail();
    }

}
