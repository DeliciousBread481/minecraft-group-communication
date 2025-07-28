package com.github.konstantyn111.crashapi.util.admin;

import com.github.konstantyn111.crashapi.entity.AdminApplication;
import com.github.konstantyn111.crashapi.entity.User;
import com.github.konstantyn111.crashapi.entity.solution.Category;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.AdminApplicationMapper;
import com.github.konstantyn111.crashapi.mapper.UserMapper;
import com.github.konstantyn111.crashapi.mapper.solution.CategoryMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.util.ErrorCode;
import org.springframework.http.HttpStatus;

public class ValidationUtils {

    public static User validateUserExists(UserMapper userMapper, Long userId, String context) {
        return userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        context + "用户不存在"));
    }

    public static Solution validateSolutionExists(SolutionMapper solutionMapper, String solutionId) {
        return solutionMapper.findById(solutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "解决方案不存在"));
    }

    public static Category validateCategoryExists(
            CategoryMapper categoryMapper, String categoryId) {
        return categoryMapper.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "问题分类不存在"));
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

    public static void validateSolutionOwnership(Solution solution, User currentUser, String operation) {
        if (!solution.getCreatedBy().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                    HttpStatus.FORBIDDEN,
                    "只能" + operation + "自己创建的解决方案哟~");
        }
    }
}