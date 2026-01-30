import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectClasses;


@Suite
@SelectClasses({
    CompileErrorStudentTest.class,
})
public class TestSuite {}
