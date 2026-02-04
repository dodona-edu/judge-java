import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dodona.junit.TabPermission;
import dodona.feedback.Permission;

@TabPermission(Permission.STAFF)
public class TabPermissionTest {
    @Test
    public void testMethod() {
        Assertions.assertEquals(true, new TabPermissions().method());
    }
}
