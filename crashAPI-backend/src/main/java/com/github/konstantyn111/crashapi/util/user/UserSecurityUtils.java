package com.github.konstantyn111.crashapi.util.user;

import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;

@Slf4j
public class UserSecurityUtils {

    /**
     * 验证开发者权限
     */
    public static User validateDeveloperPermissions(UserMapper userMapper) {
        return validateRolePermissions(userMapper, UserRoleUtils.DEVELOPER_ROLE, "开发者");
    }

    /**
     * 验证管理员权限
     */
    public static User validateAdminPermissions(UserMapper userMapper) {
        return validateRolePermissions(userMapper, UserRoleUtils.ADMIN_ROLE, "管理员");
    }

    private static User validateRolePermissions(UserMapper userMapper, String requiredRole, String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "当前用户不存在"));

        if (!userMapper.hasRole(currentUser.getId(), requiredRole)) {
            log.warn("用户：[{}] 正在访问未授权节点！我会永远看着你的~", username);
            throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                    HttpStatus.FORBIDDEN,
                    "只有" + roleName + "能执行此操作哟~");
        }
        return currentUser;
    }
}