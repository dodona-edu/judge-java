package dodona.junit;

import java.io.IOException;

public class IllegalOutputException extends IOException {
    private static final long serialVersionUID = 1;

    public IllegalOutputException() {
        super("sorry mate, no can do.");
    }

}
