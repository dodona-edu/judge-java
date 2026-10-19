import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectClasses;


@Suite
@SelectClasses({
    HungryTest.class,
})
public class TestSuite {}
