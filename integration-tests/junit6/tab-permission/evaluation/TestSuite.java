import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectClasses;


@Suite
@SelectClasses({
    TabPermissionTest.class,
})
public class TestSuite {}
