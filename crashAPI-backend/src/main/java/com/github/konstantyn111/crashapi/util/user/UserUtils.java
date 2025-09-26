package com.github.konstantyn111.crashapi.util.user;

import com.github.konstantyn111.crashapi.dto.user.AdminApplicationDTO;
import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.entity.user.AdminApplication;
import com.github.konstantyn111.crashapi.entity.user.Role;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.user.AdminApplicationMapper;
import com.github.konstantyn111.crashapi.mapper.user.RoleMapper;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

/**
 * 用户相关工具类
 */
@Slf4j
public class UserUtils {

    /**
     * 数据转换工具
     */
    public static class Convert {

        public static UserInfo toUserInfo(User user, Set<String> roles) {
            if (user == null) return null;
            return UserInfo.builder()
                    .id(user.getId())
                    .username(null)
                    .email(null)
                    .nickname(user.getNickname())
                    .avatar(null)
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .enabled(user.isEnabled())
                    .roles(roles)
                    .build();
        }

        public static AdminApplicationDTO toAdminAppDTO(AdminApplication application) {
            if (application == null) return null;

            return AdminApplicationDTO.builder()
                    .id(application.getId())
                    .applicantUsername(application.getApplicantUsername())
                    .status(application.getStatus())
                    .reason(application.getReason())
                    .feedback(application.getFeedback())
                    .processorUsername(application.getProcessorName())
                    .createdAt(application.getCreatedAt())
                    .processedAt(application.getProcessedAt())
                    .build();
        }
    }

    /**
     * 角色工具
     */
    public static class RoleUtils {
        public static final String ADMIN_ROLE = "ROLE_ADMIN";
        public static final String DEVELOPER_ROLE = "ROLE_DEV";

        public static Role getAdminRole(RoleMapper roleMapper) {
            return roleMapper.findByName(ADMIN_ROLE)
                    .orElseThrow(() -> new RuntimeException("管理员角色未配置"));
        }
    }

    /**
     * 权限安全验证
     */
    public static class Security {
        public static User validateDeveloperPermissions(UserMapper userMapper) {
            return validateRolePermissions(userMapper, RoleUtils.DEVELOPER_ROLE, "开发者");
        }

        public static void validateAdminPermissions(UserMapper userMapper) {
            validateRolePermissions(userMapper, RoleUtils.ADMIN_ROLE, "管理员");
        }

        private static User validateRolePermissions(UserMapper userMapper, String requiredRole, String roleName) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "当前用户不存在"));
            if (!userMapper.hasRole(currentUser.getId(), requiredRole)) {
                log.warn("用户：[{}] 正在访问未授权节点！", username);
                throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                        HttpStatus.FORBIDDEN,
                        "只有" + roleName + "能执行此操作哟~");
            }
            return currentUser;
        }
    }

    /**
     * 数据存在性验证
     */
    public static class Validation {
        public static void validateUserExists(UserMapper userMapper, Long userId, String context) {
            userMapper.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            context + "用户不存在"));
        }

        public static AdminApplication validateAdminApplicationStatus(
                AdminApplicationMapper mapper, Long applicationId, String requiredStatus) {
            AdminApplication application = mapper.findById(applicationId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "申请记录不存在"));
            if (!requiredStatus.equals(application.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_APPLICATION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "申请状态不可操作");
            }
            return application;
        }
    }
}
