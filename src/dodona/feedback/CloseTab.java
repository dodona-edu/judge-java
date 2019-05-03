package dodona.feedback;

public class CloseTab extends PartialFeedback {

    private Integer badgeCount;

    public CloseTab(Integer badgeCount) {
        super("close-tab");
        this.badgeCount = badgeCount;
    }

    public CloseTab() {
        this(null);
    }

}
