import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import dodona.reflection.AssertionStubber;

public class SimpleTest {

    private SimpleInterface solution;
        
    @Test
    public void test() {
        if(solution == null) solution = new Simple();
        Assert.assertEquals(1, solution.simpleMethod());
    }

}
