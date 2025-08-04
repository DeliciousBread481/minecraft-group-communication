package com.github.konstantyn111.crashapi.util.user;

import com.github.konstantyn111.crashapi.entity.user.Role;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.mapper.user.RoleMapper;

/**
 * 用户角色管理工具类
 * <p>
 * 提供角色验证和角色获取的核心功能。
 * </p>
 */
public class UserRoleUtils {

    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String DEVELOPER_ROLE = "ROLE_DEV";

    /**
     * 验证用户是否拥有指定角色
     * <p>
     * 遍历用户角色列表，检查是否包含目标角色。
     * </p>
     *
     * @param user 要验证的用户实体
     * @param roleName 目标角色名称
     * @return 用户拥有该角色返回true，否则返回false
     */
    public static boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }

    /**
     * 获取管理员角色实体
     * <p>
     * 从数据库中查询管理员角色记录，若不存在则抛出运行时异常。
     * </p>
     *
     * @param roleMapper 角色数据访问接口
     * @return 管理员角色实体
     * @throws RuntimeException 当管理员角色未配置时抛出
     */
    public static Role getAdminRole(RoleMapper roleMapper) {
        return roleMapper.findByName(UserRoleUtils.ADMIN_ROLE)
                .orElseThrow(() -> new RuntimeException("管理员角色未配置"));
    }
}