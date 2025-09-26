package com.github.konstantyn111.crashapi.security;

import com.github.konstantyn111.crashapi.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record CustomUserDetails(User user, Set<String> roleNames) implements UserDetails {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);
    private static final String ROLE_PREFIX = "ROLE_";

    public CustomUserDetails {
        Objects.requireNonNull(user, "User cannot be null");
        roleNames = roleNames != null ?
                Set.copyOf(roleNames) :
                Collections.emptySet();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleNames.stream()
                .map(role -> role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
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
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public boolean isRefreshTokenValid() {
        if (user == null) {
            logger.debug("Refresh token validation failed: user is null");
            return false;
        }

        boolean isValid = hasValidRefreshToken() && !isRefreshTokenExpired();
        if (!isValid) {
            logger.debug("Refresh token validation failed for user: {}", user.getUsername());
        }
        return isValid;
    }

    private boolean hasValidRefreshToken() {
        String token = user.getRefreshToken();
        return token != null && !token.trim().isEmpty();
    }

    private boolean isRefreshTokenExpired() {
        LocalDateTime expiry = user.getRefreshTokenExpiry();
        return expiry != null && LocalDateTime.now().isAfter(expiry);
    }

    public String getRefreshToken() {
        return user.getRefreshToken();
    }

    public boolean hasRole(String roleName) {
        String normalizedRole = roleName.startsWith(ROLE_PREFIX) ? roleName : ROLE_PREFIX + roleName;
        return roleNames.contains(normalizedRole) || roleNames.contains(roleName);
    }

    public LocalDateTime getCreatedAt() {
        return user.getCreatedAt();
    }

    public User getUser() {
        return user;
    }
}