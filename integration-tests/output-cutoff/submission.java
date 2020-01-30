import java.util.stream.Stream;
import java.util.stream.Collectors;

public class Spam {

    public String menu() {
        return Stream.of(Stream.of("Eggs"),
                         Stream.generate(() -> "spam").limit(1000),
                         Stream.of("and bacon."))
                     .flatMap(s -> s)
                     .collect(Collectors.joining(", "));
    }

}
