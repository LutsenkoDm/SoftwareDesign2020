package main.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    USER(new SimpleGrantedAuthority("USER")), ADMIN(new SimpleGrantedAuthority("ADMIN"));

    @Getter
    private GrantedAuthority authority;

    Role(GrantedAuthority authority) {
        this.authority = authority;
    }
}
