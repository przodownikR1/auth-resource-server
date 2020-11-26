package pl.java.scalatech.resourceserver;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

class DomainPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
       /* Document document = (Document) targetDomainObject;
        String p = (String) permission;
        boolean admin =
                authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(p));
        return admin ||
                document.getOwner()
                        .equals(authentication.getName());*/
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
