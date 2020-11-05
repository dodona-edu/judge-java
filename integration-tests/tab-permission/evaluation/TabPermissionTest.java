import org.junit.Assert;
import org.junit.Test;

import dodona.junit.TabPermission;
import dodona.feedback.Permission;

@TabPermission(Permission.STAFF)
public class TabPermissionTest {
    @Test
    public void testMethod() {
        Assert.assertEquals(true, new TabPermissions().method());
    }
}
