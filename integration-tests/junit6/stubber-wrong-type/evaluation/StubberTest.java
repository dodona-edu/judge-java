import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import dodona.reflection.AssertionStubber;

public class StubberTest {

    private StubberInterface solution;

    @BeforeEach public void initialize() {
        solution = new AssertionStubber().stub(StubberInterface.class, Stubber.class);
    }
        
    @Test
    public void testIntArray() {
        solution.intArray(null);
    }

    @Test
    public void testIntArrayList() {
        solution.intArrayList(null);
    }

}
