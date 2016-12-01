package dodona.feedback;

import java.util.List;
import java.util.Optional;

public class Tab extends DescribedGroup<Context> {

    private Optional<Integer> badgeCount;

    public Tab() {
        badgeCount = Optional.empty();
    }

    /* Description is Title of the tab (default Test) */
    public void setTitle(String title) {
        setDescription(Message.plain(title));
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = Optional.of(badgeCount);
    }

    private void incrementBadgeCount() {
        this.badgeCount = Optional.of(badgeCount.orElse(0) + 1);
    }

}
