package dodona.feedback;

import java.util.List;
import java.util.Optional;

public class Tab {

    private Optional<String> description; /* Title of the tab (default Test) */
    private Optional<Integer> badgeCount;
    private List<Message> messages;
    private List<Context> groups;

}
