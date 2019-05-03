package dodona.feedback;

public class StartTab extends PartialFeedback {

    private String title;
    private boolean hidden;

    public StartTab(String title) {
        this(title, false);
    }

    public StartTab(String title, boolean hidden) {
        super("start-tab");
        this.title = title;
        this.hidden = hidden;
    }

}
