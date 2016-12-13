package dodona.feedback;

import java.util.List;
import java.util.Optional;

public class Tab extends Group<Context> {

    private Optional<Integer> badgeCount;
    private Optional<String> description;

    public Tab() {
        badgeCount = Optional.empty();
    }

    /* Description is Title of the tab (default Test) */
    public void setTitle(String title) {
        this.description = Optional.ofNullable(title);
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = Optional.of(badgeCount);
    }

    private void incrementBadgeCount() {
        this.badgeCount = Optional.of(badgeCount.orElse(0) + 1);
    }

}
