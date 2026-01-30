import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectClasses;


@Suite
@SelectClasses({
    CompileErrorInTestsTest.class,
})
public class TestSuite {}
