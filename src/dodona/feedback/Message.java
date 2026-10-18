package dodona.feedback;

public class Message {

    private Format format;
    private String description;
    private Permission permission;

    public Message(Format format, String content, Permission permission) {
        this.format = format;
        this.description = content;
        this.permission = permission == Permission.STUDENT ? null : permission;
    }

    public static Message plain(String content) {
        return new Message(Format.PLAIN, content, Permission.STUDENT);
    }

    public static Message code(String content) {
        return new Message(Format.CODE, content, Permission.STUDENT);
    }

    public static Message staff(String content) {
        return new Message(Format.PLAIN, content, Permission.EDUCATOR);
    }

    public static Message internalError(String content) {
        return new Message(Format.CODE, content, Permission.EDUCATOR);
    }

}
