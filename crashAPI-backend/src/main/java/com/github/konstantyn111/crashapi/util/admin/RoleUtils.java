package com.github.konstantyn111.crashapi.util.admin;

import com.github.konstantyn111.crashapi.entity.Role;
import com.github.konstantyn111.crashapi.entity.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.UserMapper;
import com.github.konstantyn111.crashapi.util.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 角色管理
 */
public class RoleUtils {
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String DEVELOPER_ROLE = "ROLE_DEV";

    /**
     * 检查用户拥有的角色
     * @param user 用户实体
     * @param roleName 角色名称
     * @return 是否拥有传入的角色
     */
    public static boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }

    /**
     * 获取管理员角色实体
     * @param userMapper 用户数据访问接口
     * @return 管理员角色实体
     */
    public static Role getAdminRole(UserMapper userMapper) {
        return userMapper.findRoleByName(ADMIN_ROLE)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND,
                        HttpStatus.BAD_REQUEST,
                        "管理员角色不存在"));
    }

    //万一以后要用呢先留着（
    private static Role getDeveloperRole(UserMapper userMapper) {
        return userMapper.findRoleByName(DEVELOPER_ROLE)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND,
                        HttpStatus.BAD_REQUEST,
                        "开发者角色不存在"));
    }
}