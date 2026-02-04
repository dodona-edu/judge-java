
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectClasses;


@Suite
@SelectClasses({
    StubberTest.class,
})
public class TestSuite {}
