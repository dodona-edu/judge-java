import dodona.i18n.Language;
import dodona.i18n.I18nTestDescription;

import org.junit.Assert;
import org.junit.Test;

public class TranslatedTest {

    @Test
    @I18nTestDescription("translated_description")
    public void test() {
        String language = new Translated().getLanguage();
        if(Language.DUTCH == Language.current()) {
            Assert.assertEquals("nederlands", language);
        } else {
            Assert.assertEquals("english", language);
        }
    }

}
