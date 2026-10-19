import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dodona.reflection.AssertionStubber;

public class SumTest {

    private SumInterface summer() {
        return new AssertionStubber().stub(SumInterface.class, Summer.class);
    }

    @Test public void arrayListArgument() {
        Assertions.assertEquals(3, summer().sum(new ArrayList<>(List.of(1, 2))));
    }

    @Test public void immutableListArgument() {
        Assertions.assertEquals(3, summer().sum(List.of(1, 2)));
    }
}
