package com.github.konstantyn111.crashapi.security;

import com.github.konstantyn111.crashapi.entity.user.Role;
import com.github.konstantyn111.crashapi.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义用户认证详情实现
 * <p>
 * 将领域用户实体适配为Spring Security认证所需的用户详情对象，
 * 提供用户认证信息和令牌验证能力
 */
public record CustomUserDetails(User user) implements UserDetails {

    /**
     * 获取用户权限集合
     * @return 用户角色转换的权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return Collections.emptyList();
        }

        return user.getRoles().stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户密码（加密后的凭证）
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 获取用户名（唯一标识）
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 账户是否未过期（永久有效）
     */
    @Override
    public boolean isAccountNonExpired() {
        // 没有过期时间字段，默认永不过期
        return true;
    }

    /**
     * 账户是否未锁定（永久可用）
     */
    @Override
    public boolean isAccountNonLocked() {
        // 没有锁定字段，默认未锁定
        return true;
    }

    /**
     * 凭证是否未过期（永久有效）
     */
    @Override
    public boolean isCredentialsNonExpired() {
        // 没有凭证过期时间字段，默认永不过期
        return true;
    }

    /**
     * 账户是否启用
     * @return 用户启用状态
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    // ================= 自定义令牌验证方法 =================

    /**
     * 验证刷新令牌有效性
     * @return 令牌存在且未过期时返回true
     */
    public boolean isRefreshTokenValid() {
        // 检查令牌存在性和过期状态
        if (user.getRefreshToken() == null || user.getRefreshToken().isEmpty()) {
            return false;
        }

        // 如果没有设置过期时间，视为永久有效
        if (user.getRefreshTokenExpiry() == null) {
            return true;
        }

        // 检查令牌是否过期
        LocalDateTime now = LocalDateTime.now();
        return !now.isAfter(user.getRefreshTokenExpiry());
    }

    /**
     * 获取当前刷新令牌
     */
    public String getRefreshToken() {
        return user.getRefreshToken();
    }

    /**
     * 获取刷新令牌过期时间
     */
    public LocalDateTime getRefreshTokenExpiry() {
        return user.getRefreshTokenExpiry();
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * 获取用户邮箱
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 获取用户角色名称集合
     */
    public Set<String> getRoleNames() {
        if (user.getRoles() == null) {
            return Collections.emptySet();
        }
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * 检查用户是否拥有指定角色
     * @param roleName 角色名称 (如 "ROLE_ADMIN")
     */
    public boolean hasRole(String roleName) {
        if (user.getRoles() == null) {
            return false;
        }
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * 获取用户创建时间
     */
    public LocalDateTime getCreatedAt() {
        return user.getCreatedAt();
    }

    /**
     * 获取用户最后更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return user.getUpdatedAt();
    }

    /**
     * 获取底层用户实体
     */
    public User getUser() {
        return user;
    }
}