import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import dodona.reflection.AssertionStubber;

public class TestWriterTest {

    private static dodona.junit.TestWriter testWriter = new dodona.junit.TestWriter();

    private TestWriterInterface solution;

    @BeforeEach public void initialize() {
        solution = new AssertionStubber().stub(TestWriterInterface.class, TestWriter.class);
    }
        
    @Test
    public void test() {
        testWriter.compare("hello", solution.hello());
    }

}
