
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    // List of tests to run.
    SimpleTest.class,
    GeneratedTest.class,
})
public class TestSuite {}
