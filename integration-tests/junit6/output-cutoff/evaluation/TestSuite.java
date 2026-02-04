
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectClasses;


@Suite
@SelectClasses({
    SpamTest.class,
})
public class TestSuite {}
