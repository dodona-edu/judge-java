import java.util.ArrayList;
import java.util.List;

public class Hungry {

    private static final List<long[]> KEPT = new ArrayList<>();

    public void eat() {
        while (true) {
            KEPT.add(new long[1024]);
        }
    }

}
