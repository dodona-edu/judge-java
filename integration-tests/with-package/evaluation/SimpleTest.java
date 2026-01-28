import org.junit.Assert;
import org.junit.Test;

import dodona.junit.TabTitle;
import dodona.junit.TestDescription;

@TabTitle("Tab title")
public class SimpleTest {

    @Test
    @TestDescription("Test description")
    public void test() {
        Assert.assertEquals(new Translated().getLanguage(), "english");
    }

}
