import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;
import dodona.junit.MessageWriter;

public class SubmissionTest {

    @RegisterExtension
    public MessageWriter out = new MessageWriter();

    @Test
    public void testAdd() {
        int a = 5;
        int b = 10;
        int expected = 15;

        out.println("Testing add with arguments:");
        out.println("a = " + a);
        out.println("b = " + b);

        int result = Submission.add(a, b);

        out.println("Result obtained: " + result);

        Assertions.assertEquals(expected, result, "The sum is incorrect");
    }

}
