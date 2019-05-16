package dodona.feedback;

import com.google.gson.annotations.SerializedName;

public class StatusPair {

    @SerializedName("enum") private final Status enum_;
    private final String human;

    public StatusPair(Status enum_, String human) {
        this.enum_ = enum_;
        this.human = human;
    }

}
