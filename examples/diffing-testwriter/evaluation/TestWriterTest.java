

import dodona.reflection.AssertionStubber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestWriterTest {

    private static dodona.junit.TestWriter testWriter = new dodona.junit.TestWriter();

    private TestWriterInterface solution;

    @BeforeEach
    public void initialize() {
        solution = new AssertionStubber().stub(TestWriterInterface.class, TestWriter.class);
    }
        
    @Test
    public void test() {
        testWriter.compare("hello", solution.hello());
    }

}
