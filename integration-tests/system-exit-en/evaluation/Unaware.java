import org.junit.Assert;
import org.junit.Test;

public class Unaware {

    @Test
    public void exit0ShouldFail() {
        new Exitter().exit0();
    }

    @Test
    public void exit1ShouldFail() {
        new Exitter().exit1();
    }

    @Test
    public void noExitShouldPass() {
        new Exitter().noExit();
    }

}
