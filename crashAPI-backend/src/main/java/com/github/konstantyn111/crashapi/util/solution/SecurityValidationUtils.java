package com.github.konstantyn111.crashapi.util.solution;

import com.github.konstantyn111.crashapi.entity.solution.Category;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.solution.CategoryMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import com.github.konstantyn111.crashapi.util.user.UserRoleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全验证与数据验证工具类
 * <p>
 * 提供权限验证和数据存在性验证的静态工具方法。
 * </p>
 */
@Slf4j
public class SecurityValidationUtils {

    // ==================== 安全验证 ====================

    /**
     * 验证当前用户是否具有开发者权限
     * <p>
     * 从安全上下文中提取认证信息，验证用户是否拥有ROLE_DEV角色。
     * </p>
     *
     * @param userMapper 用户数据访问接口
     * @return 当前登录用户实体
     * @throws BusinessException 当用户不存在或权限不足时抛出
     */
    public static User validateDeveloperPermissions(UserMapper userMapper) {
        return validateRolePermissions(userMapper, UserRoleUtils.DEVELOPER_ROLE, "开发者");
    }

    /**
     * 验证当前用户是否具有管理员权限
     * <p>
     * 从安全上下文中提取认证信息，验证用户是否拥有ROLE_ADMIN角色。
     * </p>
     *
     * @param userMapper 用户数据访问接口
     * @return 当前登录用户实体
     * @throws BusinessException 当用户不存在或权限不足时抛出
     */
    public static User validateAdminPermissions(UserMapper userMapper) {
        return validateRolePermissions(userMapper, UserRoleUtils.ADMIN_ROLE, "管理员");
    }

    /**
     * 验证当前用户是否拥有指定角色权限
     * <p>
     * 核心权限验证方法，检查当前认证用户是否拥有指定角色。
     * </p>
     *
     * @param userMapper 用户数据访问接口
     * @param requiredRole 需要验证的角色名称
     * @param roleName 角色显示名称（用于错误信息）
     * @return 当前登录用户实体
     * @throws BusinessException 当用户不存在或权限不足时抛出
     */
    private static User validateRolePermissions(UserMapper userMapper, String requiredRole, String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "当前用户不存在"));

        if (!UserRoleUtils.hasRole(currentUser, requiredRole)) {
            log.warn("用户：[{}] 正在访问未授权节点！我会永远看着你的~", username);
            throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                    HttpStatus.FORBIDDEN,
                    "只有" + roleName + "能执行此操作哟~");
        }
        return currentUser;
    }

    // ==================== 数据验证 ====================

    /**
     * 验证解决方案是否存在
     * <p>
     * 根据解决方案ID查询数据库，验证解决方案记录是否存在。
     * </p>
     *
     * @param solutionMapper 解决方案数据访问接口
     * @param solutionId 要验证的解决方案ID
     * @return 存在的解决方案实体
     * @throws BusinessException 当解决方案不存在时抛出
     */
    public static Solution validateSolutionExists(SolutionMapper solutionMapper, String solutionId) {
        return solutionMapper.findById(solutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "解决方案不存在"));
    }

    /**
     * 验证问题分类是否存在
     * <p>
     * 根据分类ID查询数据库，验证问题分类记录是否存在。
     * </p>
     *
     * @param categoryMapper 分类数据访问接口
     * @param categoryId 要验证的分类ID
     * @return 存在的分类实体
     * @throws BusinessException 当分类不存在时抛出
     */
    public static Category validateCategoryExists(CategoryMapper categoryMapper, String categoryId) {
        return categoryMapper.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "问题分类不存在"));
    }

    /**
     * 验证解决方案所有权
     * <p>
     * 检查当前用户是否为解决方案的创建者。
     * </p>
     *
     * @param solution 要验证的解决方案实体
     * @param currentUser 当前登录用户实体
     * @param operation 当前操作名称（用于错误信息）
     * @throws BusinessException 当用户不是解决方案创建者时抛出
     */
    public static void validateSolutionOwnership(Solution solution, User currentUser, String operation) {
        if (!solution.getCreatedBy().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                    HttpStatus.FORBIDDEN,
                    "只能" + operation + "自己创建的解决方案哟~");
        }
    }
}