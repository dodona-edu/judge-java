package dodona.reflection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssertionStubberTest {

    public interface Sum {
        int sum(List<Integer> values);
    }

    public static class SumOfCollection {
        public int sum(Collection<Integer> values) {
            return values.stream().mapToInt(Integer::intValue).sum();
        }
    }

    public static class SumOfArrayList {
        public int sum(ArrayList<Integer> values) {
            return values.stream().mapToInt(Integer::intValue).sum();
        }
    }

    @Test
    void acceptsAStudentParameterThatIsASupertypeOfTheInterfacesParameter() {
        Sum sum = new AssertionStubber().stub(Sum.class, SumOfCollection.class);
        assertEquals(3, sum.sum(List.of(1, 2)));
    }

    @Test
    void reportsAMethodWhoseParameterIsASubtypeOfTheInterfacesParameterAsMissing() {
        Sum sum = new AssertionStubber().stub(Sum.class, SumOfArrayList.class);
        AssertionError error = assertThrows(AssertionError.class, () -> sum.sum(List.of(1, 2)));
        assertEquals("Missing method: int sum(List).", error.getMessage());
    }
}
