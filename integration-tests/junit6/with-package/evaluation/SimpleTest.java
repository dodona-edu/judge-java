import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dodona.junit.TabTitle;
import dodona.junit.TestDescription;

@TabTitle("Tab title")
public class SimpleTest {

    @Test
    @TestDescription("Test description")
    public void test() {
        Assertions.assertEquals(new Translated().getLanguage(), "english");
    }

}
