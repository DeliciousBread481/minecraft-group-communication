package com.github.konstantyn111.crashapi.util;

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
import org.owasp.encoder.Encode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 安全验证与数据验证工具类
 */
@Slf4j
@Component
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

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(\\b(union|select|insert|update|delete|drop|alter|create|exec|execute|truncate|declare|xp_|sp_)\\b|(--)|(;))"
    );

    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(<script|javascript:|onload|onerror|onclick|onmouseover|eval\\(|alert\\(|document\\.cookie|vbscript:|<iframe|<img|<svg|onload)"
    );

    /**
     * 检查是否存在SQL注入风险
     * @param input 输入字符串
     * @return 是否存在风险
     */
    public boolean hasSqlInjectionRisk(String input) {
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * 检查是否存在XSS风险
     * @param input 输入字符串
     * @return 是否存在风险
     */
    public boolean hasXssRisk(String input) {
        return XSS_PATTERN.matcher(input).find();
    }

    /**
     * 对输入进行XSS过滤
     * @param input 输入字符串
     * @return 过滤后的安全字符串
     */
    public String sanitizeForXss(String input) {
        return Encode.forHtmlContent(input);
    }

    /**
     * 对输入进行SQL注入过滤
     * @param input 输入字符串
     * @return 过滤后的安全字符串
     */
    public String sanitizeForSql(String input) {
        return SQL_INJECTION_PATTERN.matcher(input).replaceAll("");
    }

    /**
     * 全面安全检查
     * @param input 输入字符串
     * @return 安全检查结果
     * @throws BusinessException 如果发现安全风险
     */
    public String fullSecurityCheck(String input) {
        if (hasSqlInjectionRisk(input)) {
            throw new BusinessException(ErrorCode.SECURITY_VIOLATION,
                    HttpStatus.BAD_REQUEST, "输入内容存在风险！");
        }

        if (hasXssRisk(input)) {
            throw new BusinessException(ErrorCode.SECURITY_VIOLATION,
                    HttpStatus.BAD_REQUEST, "输入内容存在风险！");
        }
        return sanitizeForXss(sanitizeForSql(input));
    }
}
