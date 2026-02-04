
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectClasses;


@Suite
@SelectClasses({
    SleepyTest.class,
})
public class TestSuite {}
