package com.github.konstantyn111.crashapi.util.user;

import com.github.konstantyn111.crashapi.entity.user.AdminApplication;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.user.AdminApplicationMapper;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import org.springframework.http.HttpStatus;

/**
 * 用户验证工具类
 * <p>
 * 提供用户和管理员申请相关的验证功能，用于业务逻辑层的数据校验。
 * </p>
 */
public class UserValidationUtils {

    /**
     * 验证用户是否存在
     * <p>
     * 根据用户ID查询数据库，验证用户记录是否存在。
     * </p>
     *
     * @param userMapper 用户数据访问接口
     * @param userId 要验证的用户ID
     * @param context 验证上下文（用于错误信息前缀）
     * @return 存在的用户实体
     * @throws BusinessException 当用户不存在时抛出404错误
     */
    public static User validateUserExists(UserMapper userMapper, Long userId, String context) {
        return userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        context + "用户不存在"));
    }

    /**
     * 验证管理员申请状态
     * <p>
     * 根据申请ID查询申请记录，并验证其状态是否符合要求。
     * </p>
     *
     * @param mapper 管理员申请数据访问接口
     * @param applicationId 要验证的申请ID
     * @param requiredStatus 要求的申请状态（如"PENDING"）
     * @return 存在的管理员申请实体
     * @throws BusinessException 当申请不存在或状态不符时抛出
     */
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