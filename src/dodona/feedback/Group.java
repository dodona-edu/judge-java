package dodona.feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Group<T> {

    private List<Message> messages = new ArrayList<>();
    private List<T> groups         = new ArrayList<>();

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void addMessage(String message) {
        this.messages.add(Message.plain(message));
    }

    public void clearMessages() {
        this.messages.clear();
    }

    public void addChild(T child) {
        this.groups.add(child);
    }

    public void prependChild(T child) {
        this.groups.add(0, child);
    }

    public T lastChild() {
        return this.groups.get(this.groups.size() - 1);
    }

    public Stream<T> children() {
        return this.groups.stream();
    }

}
