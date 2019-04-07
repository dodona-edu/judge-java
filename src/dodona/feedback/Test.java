package dodona.feedback;

import java.util.List;

public class Test {

    private Message description;
    private boolean accepted;
    private String expected; /* ignored (assumed equal to generated) if accepted */
    private String generated;
    private List<Message> messages; /* not used by Java judge */

    public Test(Message description, boolean accepted, String expected, String generated) {
        this.description = description;
        this.accepted = accepted;
        this.expected = expected;
        this.generated = generated;
    }

}
