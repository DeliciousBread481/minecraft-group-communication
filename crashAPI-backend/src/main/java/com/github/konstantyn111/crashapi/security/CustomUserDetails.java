package com.github.konstantyn111.crashapi.security;

import com.github.konstantyn111.crashapi.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record CustomUserDetails(User user, Set<String> roleNames) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roleNames == null || roleNames.isEmpty()) {
            return Collections.emptyList();
        }
        return roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public boolean isRefreshTokenValid() {
        if (user.getRefreshToken() == null || user.getRefreshToken().isEmpty()) {
            return false;
        }
        if (user.getRefreshTokenExpiry() == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        return !now.isAfter(user.getRefreshTokenExpiry());
    }

    public String getRefreshToken() {
        return user.getRefreshToken();
    }

    public Long getUserId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public boolean hasRole(String roleName) {
        return roleNames != null && roleNames.contains(roleName);
    }

    public LocalDateTime getCreatedAt() {
        return user.getCreatedAt();
    }

    public User getUser() {
        return user;
    }
}
