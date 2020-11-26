package pl.java.scalatech.resourceserver;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

class PermissionService {

    public boolean hasPermission(String permission) {
        if (StringUtils.hasText(permission)) {
            return false;
        }
        Authentication authentication = getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                          .map(GrantedAuthority::getAuthority)
                          .filter(StringUtils::hasText)
                          .anyMatch(x -> PatternMatchUtils.simpleMatch(permission, x));
    }
}