package dodona.feedback;

import java.util.List;
import java.util.Optional;

public class Context {

    private boolean accepted;
    private Optional<Message> description;
    private List<Message> messages;
    private List<Testcase> groups;

}
