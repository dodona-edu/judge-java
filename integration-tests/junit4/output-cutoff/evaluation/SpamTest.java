import org.junit.Assert;
import org.junit.Test;

public class SpamTest {

    private static dodona.junit.TestWriter testWriter = new dodona.junit.TestWriter();

    @Test
    public void test() {
        testWriter.compare("Eggs, and bacon.", new Spam().menu());
    }

}
