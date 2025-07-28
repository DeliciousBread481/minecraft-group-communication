package com.github.konstantyn111.crashapi.util.admin;

import com.github.konstantyn111.crashapi.entity.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.UserMapper;
import com.github.konstantyn111.crashapi.util.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;

/**
 * 用户安全验证！！！
 */
@Slf4j
public class SecurityUtils {

    /**
     * 验证开发者权限
     * @param userMapper 用户数据接口
     * @return 当前开发者用户
     */
    public static User validateDeveloperPermissions(UserMapper userMapper) {
        return validateRolePermissions(userMapper, RoleUtils.DEVELOPER_ROLE, "开发者");
    }

    /**
     * 验证管理员权限
     * @param userMapper 用户数据接口
     * @return 当前管理员用户
     */
    public static User validateAdminPermissions(UserMapper userMapper) {
        return validateRolePermissions(userMapper, RoleUtils.ADMIN_ROLE, "管理员");
    }

    /**
     * 验证用户权限
     * @param userMapper 用户数据接口
     * @param requiredRole 权限等级
     * @param roleName 权限名称
     * @return 当前用户
     */
    private static User validateRolePermissions(UserMapper userMapper, String requiredRole, String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "当前用户不存在"));

        boolean hasRole = RoleUtils.hasRole(currentUser, requiredRole);

        if (!hasRole) {
            log.warn("用户：[{}] 正在访问未授权节点！我会永远看着你的~",username);
            throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                    HttpStatus.FORBIDDEN,
                    "只有" + roleName + "能执行此操作哟~");
        }

        return currentUser;
    }
}