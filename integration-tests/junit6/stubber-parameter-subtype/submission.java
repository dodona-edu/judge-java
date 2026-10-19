import java.util.ArrayList;

public class Summer {
    public int sum(ArrayList<Integer> values) {
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return total;
    }
}
