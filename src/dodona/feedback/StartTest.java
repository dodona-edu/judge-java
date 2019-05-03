package dodona.feedback;

public class StartTest extends DescribedStart {

    private String expected;

    public StartTest(String expected) {
        this(expected, null);
    }

    public StartTest(String expected, Message description) {
        super("start-test", description);
        this.expected = expected;
    }

}
