package dodona.feedback;

public class StartTab extends PartialFeedback {

    private String title;
    private boolean hidden;
    private Permission permission;

    public StartTab(String title) {
        this(title, false, Permission.STUDENT);
    }

    public StartTab(String title, boolean hidden) {
        this(title, hidden, Permission.STUDENT);
    }

    public StartTab(String title, Permission permission) {
        this(title, false, permission);
    }

    public StartTab(String title, boolean hidden, Permission permission) {
        super("start-tab");
        this.title = title;
        this.hidden = hidden;
        this.permission = permission;
    }

}
