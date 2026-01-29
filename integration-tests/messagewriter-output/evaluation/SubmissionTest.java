import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import dodona.junit.MessageWriter;

public class SubmissionTest {

    @Rule
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

        Assert.assertEquals("The sum is incorrect", expected, result);
    }

}
