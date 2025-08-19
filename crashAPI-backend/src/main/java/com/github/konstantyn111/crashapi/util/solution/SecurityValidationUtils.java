package com.github.konstantyn111.crashapi.util.solution;

import com.github.konstantyn111.crashapi.entity.solution.Category;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.solution.CategoryMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import com.github.konstantyn111.crashapi.security.CustomUserDetails;
import com.github.konstantyn111.crashapi.util.user.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全验证与数据验证工具类
 */
@Slf4j
public class SecurityValidationUtils {

    // ==================== 安全验证 ====================

    /**
     * 验证当前用户是否具有开发者权限
     */
    public static User validateDeveloperPermissions(UserMapper userMapper) {
        return validateRolePermissions(userMapper, UserUtils.RoleUtils.DEVELOPER_ROLE, "开发者");
    }

    /**
     * 验证当前用户是否具有管理员权限
     */
    public static User validateAdminPermissions(UserMapper userMapper) {
        return validateRolePermissions(userMapper, UserUtils.RoleUtils.ADMIN_ROLE, "管理员");
    }

    /**
     * 验证当前用户是否拥有指定角色权限
     */
    private static User validateRolePermissions(UserMapper userMapper, String requiredRole, String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED,
                    HttpStatus.UNAUTHORIZED,
                    "用户未认证");
        }

        User currentUser = userMapper.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "当前用户不存在"));

        if (!userDetails.hasRole(requiredRole)) {
            log.warn("用户：[{}] 正在访问未授权节点！我会永远看着你的~", userDetails.getUsername());
            throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                    HttpStatus.FORBIDDEN,
                    "只有" + roleName + "能执行此操作哟~");
        }
        return currentUser;
    }

    // ==================== 数据验证 ====================

    public static Solution validateSolutionExists(SolutionMapper solutionMapper, String solutionId) {
        return solutionMapper.findById(solutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "解决方案不存在"));
    }

    public static Category validateCategoryExists(CategoryMapper categoryMapper, String categoryId) {
        return categoryMapper.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "问题分类不存在"));
    }

    public static void validateSolutionOwnership(Solution solution, User currentUser, String operation) {
        if (!solution.getCreatedBy().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                    HttpStatus.FORBIDDEN,
                    "只能" + operation + "自己创建的解决方案哟~");
        }
    }
}
