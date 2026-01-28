import org.junit.Assert;
import org.junit.Test;

import dodona.junit.ExitException;

public class Usage {

    @Test
    public void exit0IsOK() {
        try {
            new Exitter().exit0();
        } catch(ExitException ee) {
            Assert.assertEquals(0, ee.getStatus());
        }
    }

    @Test
    public void exit1IsNotOK() {
        try {
            new Exitter().exit1();
        } catch(ExitException ee) {
            Assert.assertEquals(0, ee.getStatus());
        }
    }

}
