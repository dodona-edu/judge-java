package dodona.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import dodona.feedback.Permission;

@Retention(RetentionPolicy.RUNTIME)
public @interface TabPermission {
    Permission DEFAULT = Permission.STUDENT;
    
    Permission value();
}
