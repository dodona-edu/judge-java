import org.junit.Assert;
import org.junit.Test;
import org.junit.Rule;

import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class Interference {

    @Rule public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void exit0IsOK() {
        exit.expectSystemExit();
        new Exitter().exit0();
    }

    @Test
    public void exit1shouldFail() {
        exit.expectSystemExitWithStatus(0);
        new Exitter().exit1();
    }

    @Test
    public void noExitShouldPass() {
        new Exitter().noExit();
    }

}
