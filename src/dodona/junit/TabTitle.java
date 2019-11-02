package dodona.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME)
public @interface TabTitle {
    String DEFAULT = "Test";
    
    String value();
}
