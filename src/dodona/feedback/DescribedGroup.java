package dodona.feedback;

import java.util.Optional;

public class DescribedGroup<T> extends Group<T> {

    private Optional<Message> description = Optional.empty();

    public void setDescription(Message description) {
        this.description = Optional.ofNullable(description);
    }

}
