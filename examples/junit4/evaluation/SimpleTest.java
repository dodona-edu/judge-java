import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

    private SimpleInterface solution;
        
    @Test
    public void testZero() {
        if(solution == null) solution = new Simple();
        Assert.assertEquals(1, solution.addOne(0));
    }

    @Test
    public void testOne() {
        if(solution == null) solution = new Simple();
        Assert.assertEquals(2, solution.addOne(1));
    }

}
