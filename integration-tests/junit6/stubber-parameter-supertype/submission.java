import java.util.Collection;

public class Summer {
    public int sum(Collection<Integer> values) {
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return total;
    }
}
