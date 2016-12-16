package dodona.feedback;

import java.util.List;
import java.util.ArrayList;

public class Group<T> {

    private List<Message> messages = new ArrayList<>();
    private List<T> groups         = new ArrayList<>();

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void addMessage(String message) {
        this.messages.add(Message.plain(message));
    }

    public void addChild(T child) {
        this.groups.add(child);
    }

    public void prependChild(T child) {
        this.groups.add(0, child);
    }

}
