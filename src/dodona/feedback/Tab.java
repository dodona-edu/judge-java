package dodona.feedback;

public class Tab extends Group<Context> {

    private int badgeCount;
    private String description;

    public Tab() {
        badgeCount = 0;
    }

    /* Description is Title of the tab (default Test) */
    public void setTitle(String title) {
        this.description = title;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    public void incrementBadgeCount() {
        this.badgeCount += 1;
    }

    public void decrementBadgeCount() {
        this.badgeCount -= 1;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

}
