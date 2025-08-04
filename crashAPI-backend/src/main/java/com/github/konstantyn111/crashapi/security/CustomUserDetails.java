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
     * <p>
     * 将用户角色转换为Spring Security的GrantedAuthority集合。
     * </p>
     *
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
     * 获取用户密码凭证
     * <p>
     * 返回数据库中存储的加密密码。
     * </p>
     *
     * @return 加密后的密码字符串
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 获取用户名标识
     * <p>
     * 返回用户的唯一登录标识。
     * </p>
     *
     * @return 用户名
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 验证账户是否未过期
     * <p>
     * 当前系统未实现账户过期机制，默认返回true。
     * </p>
     *
     * @return 账户未过期返回true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 验证账户是否未锁定
     * <p>
     * 当前系统未实现账户锁定机制，默认返回true。
     * </p>
     *
     * @return 账户未锁定返回true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 验证凭证是否未过期
     * <p>
     * 当前系统未实现凭证过期机制，默认返回true。
     * </p>
     *
     * @return 凭证未过期返回true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 验证账户是否启用
     * <p>
     * 返回用户实体的启用状态。
     * </p>
     *
     * @return 账户启用状态
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    // ================= 自定义令牌验证方法 =================

    /**
     * 验证刷新令牌有效性
     * <p>
     * 检查令牌存在性及是否过期。
     * </p>
     *
     * @return 令牌存在且未过期时返回true
     */
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

    /**
     * 获取当前刷新令牌
     *
     * @return 刷新令牌字符串
     */
    public String getRefreshToken() {
        return user.getRefreshToken();
    }

    /**
     * 获取刷新令牌过期时间
     *
     * @return 令牌过期时间戳
     */
    public LocalDateTime getRefreshTokenExpiry() {
        return user.getRefreshTokenExpiry();
    }

    /**
     * 获取用户ID
     *
     * @return 用户唯一标识符
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * 获取用户邮箱
     *
     * @return 用户注册邮箱
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 获取用户角色名称集合
     *
     * @return 用户角色名称集合
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
     *
     * @param roleName 角色名称 (如 "ROLE_ADMIN")
     * @return 用户拥有该角色返回true
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
     *
     * @return 用户账户创建时间
     */
    public LocalDateTime getCreatedAt() {
        return user.getCreatedAt();
    }

    /**
     * 获取用户最后更新时间
     *
     * @return 用户信息最后更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return user.getUpdatedAt();
    }

    /**
     * 获取底层用户实体
     *
     * @return 封装的用户实体
     */
    public User getUser() {
        return user;
    }
}