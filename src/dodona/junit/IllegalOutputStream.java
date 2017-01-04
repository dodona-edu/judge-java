package dodona.junit;

import java.io.OutputStream;

public class IllegalOutputStream extends OutputStream {

    public void write(int b) throws IllegalOutputException {
        throw new IllegalOutputException();
    }

}
