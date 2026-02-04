import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import dodona.reflection.AssertionStubber;

public class TestWriterTest {

    private static dodona.junit.TestWriter testWriter = new dodona.junit.TestWriter();

    private TestWriterInterface solution;

    @Before public void initialize() {
        solution = new AssertionStubber().stub(TestWriterInterface.class, TestWriter.class);
    }
        
    @Test
    public void test() {
        testWriter.compare("hello", solution.hello());
    }

}
