import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpamTest {

    private static dodona.junit.TestWriter testWriter = new dodona.junit.TestWriter();

    @Test
    public void test() {
        testWriter.compare("Eggs, and bacon.", new Spam().menu());
    }

}
