import java.util.ArrayList;

public class Stubber {

    private Object o;

    private Stubber() { o = null; }
    public Stubber(int i) { o = i; }
    public Stubber(A a) { o = null; }
    public Stubber(B b) { o = b; }

    public Object test() { return o; }

}
