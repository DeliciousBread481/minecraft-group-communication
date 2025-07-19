package com.github.konstantyn111.crashapi.security;

import com.github.konstantyn111.crashapi.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
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
        return true;
    }

    /**
     * 账户是否未锁定（永久可用）
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证是否未过期（永久有效）
     */
    @Override
    public boolean isCredentialsNonExpired() {
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

        Date now = new Date();
        return user.getRefreshTokenExpiry() == null || !user.getRefreshTokenExpiry().before(now);
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