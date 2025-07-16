package com.github.konstantyn111.crashapi.security;

import com.github.konstantyn111.crashapi.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public record CustomUserDetails(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return Collections.emptyList();
        }
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
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

    /**
     * 检查刷新令牌是否有效
     */
    public boolean isRefreshTokenValid() {
        // 检查令牌存在性和过期状态
        if (user.getRefreshToken() == null || user.getRefreshToken().isEmpty()) {
            return false;
        }

        Date now = new Date();
        if (user.getRefreshTokenExpiry() != null && user.getRefreshTokenExpiry().before(now)) {
            return false;
        }

        return true;
    }

    /**
     * 获取刷新令牌
     */
    public String getRefreshToken() {
        return user.getRefreshToken();
    }

    /**
     * 获取刷新令牌过期时间
     */
    public Date getRefreshTokenExpiry() {
        return user.getRefreshTokenExpiry();
    }

    /**
     * 获取底层用户实体
     */
    public User getUser() {
        return user;
    }
}