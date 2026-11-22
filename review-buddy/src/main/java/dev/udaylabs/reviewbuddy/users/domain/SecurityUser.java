package dev.udaylabs.reviewbuddy.users.domain;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Authenticated principal. Public only because Thymeleaf reads {@code displayName} via reflection
 * for the navbar; no other module references this type.
 */
public class SecurityUser extends User {

    private final String displayName;

    SecurityUser(String email, String password, String displayName, String role) {
        super(email, password, List.of(new SimpleGrantedAuthority(role)));
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
