package dodona.feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Testcase {

    private Message description    = null;
    private boolean accepted       = false;
    private List<Message> messages = new ArrayList<>();
    private List<Test> tests       = new ArrayList<>();

    public void setDescription(Message description) {
        this.description = description;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void addMessage(String message) {
        this.messages.add(Message.plain(message));
    }

    public void clearMessages() {
        this.messages.clear();
    }

    public void addChild(Test child) {
        this.tests.add(child);
    }

    public void prependChild(Test child) {
        this.tests.add(0, child);
    }

    public Test lastChild() {
        return this.tests.get(this.tests.size() - 1);
    }

    public Stream<Test> children() {
        return this.tests.stream();
    }

}
