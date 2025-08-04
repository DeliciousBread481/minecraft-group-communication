package com.github.konstantyn111.crashapi.service.user;

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
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.user.UserConvertUtil;
import com.github.konstantyn111.crashapi.util.user.UserRoleUtils;
import com.github.konstantyn111.crashapi.util.user.UserSecurityUtils;
import com.github.konstantyn111.crashapi.util.user.UserValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final UserMapper userMapper;
    private final AdminApplicationMapper adminApplicationMapper;
    private final RoleMapper roleMapper;

    /**
     * 分页获取所有用户信息（需要开发者权限）
     * <p>
     * 查询包含角色信息的用户列表，结果按分页参数返回。此操作需要开发者权限。
     * 系统异常时返回500错误。
     * </p>
     *
     * @param pageable 分页参数（页大小、偏移量等）
     * @return 分页的用户信息列表响应实体
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional(readOnly = true)
    public RestResponse<Page<UserInfo>> getAllUsers(Pageable pageable) {
        try {
            List<User> users = userMapper.findAllWithRolesPaged(
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );

            List<UserInfo> userInfos = users.stream()
                    .map(UserConvertUtil::convertToUserInfo)
                    .collect(Collectors.toList());

            long total = userMapper.countAllUsers();

            Page<UserInfo> page = new PageImpl<>(userInfos, pageable, total);
            return RestResponse.success(page, "获取用户列表成功");
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取用户列表失败: " + ex.getMessage());
        }
    }

    /**
     * 将用户提升为管理员（需要开发者权限）
     * <p>
     * 为目标用户添加管理员角色。若用户已是管理员将抛出异常。
     * 此操作需要开发者权限，并验证当前开发者状态。
     * </p>
     *
     * @param userId 要提升权限的用户ID
     * @return 操作结果响应（无数据体）
     * @throws BusinessException 当用户不存在、已是管理员或操作失败时抛出
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> promoteToAdmin(Long userId) {
        try {
            User currentDev = UserSecurityUtils.validateDeveloperPermissions(userMapper);

            User targetUser = UserValidationUtils.validateUserExists(userMapper, userId, "提升权限-");

            if (userMapper.hasRole(userId, UserRoleUtils.ADMIN_ROLE)) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "用户已是管理员");
            }

            Role adminRole = UserRoleUtils.getAdminRole(roleMapper);
            userMapper.addRoleToUser(userId, adminRole.getId());

            return RestResponse.success("用户已成功提升为管理员");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "提升用户为管理员失败: " + ex.getMessage());
        }
    }

    /**
     * 撤销用户的管理员权限（需要开发者权限）
     * <p>
     * 移除目标用户的管理员角色。若用户不是管理员将抛出异常。
     * 此操作需要开发者权限。
     * </p>
     *
     * @param userId 要撤销权限的用户ID
     * @return 操作结果响应（无数据体）
     * @throws BusinessException 当用户不存在、不是管理员或操作失败时抛出
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> revokeAdminRole(Long userId) {
        try {
            UserSecurityUtils.validateDeveloperPermissions(userMapper);
            User targetUser = UserValidationUtils.validateUserExists(userMapper, userId, "撤销权限-");

            if (!userMapper.hasRole(userId, UserRoleUtils.ADMIN_ROLE)) {
                throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
                        HttpStatus.BAD_REQUEST,
                        "用户不是管理员");
            }

            Role adminRole = UserRoleUtils.getAdminRole(roleMapper);
            userMapper.removeRoleFromUser(userId, adminRole.getId());

            return RestResponse.success("已成功撤销用户的管理员权限");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "撤销管理员权限失败: " + ex.getMessage());
        }
    }

    /**
     * 获取待处理的管理员申请列表（需要开发者权限）
     * <p>
     * 查询状态为PENDING的申请记录，包含关联的用户信息，结果按分页返回。
     * 此操作需要开发者权限。
     * </p>
     *
     * @param pageable 分页参数
     * @return 分页的申请信息列表响应实体
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional(readOnly = true)
    public RestResponse<Page<AdminApplicationDTO>> getPendingApplications(Pageable pageable) {
        try {
            List<AdminApplication> applications = adminApplicationMapper
                    .findPendingApplicationsWithUserInfo(pageable.getPageSize(), (int) pageable.getOffset());

            long total = adminApplicationMapper.countPendingApplications();

            List<AdminApplicationDTO> dtos = applications.stream()
                    .map(UserConvertUtil::convertToAdminAppDTO)
                    .collect(Collectors.toList());

            Page<AdminApplicationDTO> page = new PageImpl<>(dtos, pageable, total);
            return RestResponse.success(page, "获取申请列表成功");
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取申请列表失败: " + ex.getMessage());
        }
    }

    /**
     * 批准管理员申请（需要开发者权限）
     * <p>
     * 将申请状态改为APPROVED并为关联用户添加管理员角色。
     * 会记录处理者和处理时间，此操作需要开发者权限。
     * </p>
     *
     * @param applicationId 要批准的申请ID
     * @return 操作结果响应（无数据体）
     * @throws BusinessException 当申请不存在/非待处理状态、关联用户不存在或用户已是管理员时抛出
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> approveApplication(Long applicationId) {
        try {
            User currentDev = UserSecurityUtils.validateDeveloperPermissions(userMapper);

            AdminApplication application = UserValidationUtils.validateAdminApplicationStatus(
                    adminApplicationMapper, applicationId, "PENDING");

            User targetUser = userMapper.findByIdWithRoles(application.getUserId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND, "申请关联的用户不存在"));

            if (userMapper.hasRole(targetUser.getId(), UserRoleUtils.ADMIN_ROLE)) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "用户已是管理员");
            }

            Role adminRole = UserRoleUtils.getAdminRole(roleMapper);
            userMapper.addRoleToUser(application.getUserId(), adminRole.getId());

            application.setStatus("APPROVED");
            application.setProcessorId(currentDev.getId());
            application.setProcessedAt(LocalDateTime.now());
            adminApplicationMapper.update(application);

            return RestResponse.success("申请已批准");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "批准申请失败: " + ex.getMessage());
        }
    }

    /**
     * 拒绝管理员申请（需要开发者权限）
     * <p>
     * 将申请状态改为REJECTED，可附加拒绝原因。
     * 会记录处理者和处理时间，此操作需要开发者权限。
     * </p>
     *
     * @param applicationId 要拒绝的申请ID
     * @param reason 拒绝原因（可选，默认为"申请不符合要求"）
     * @return 操作结果响应（无数据体）
     * @throws BusinessException 当申请不存在/非待处理状态时抛出
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> rejectApplication(Long applicationId, String reason) {
        try {
            User currentDev = UserSecurityUtils.validateDeveloperPermissions(userMapper);

            AdminApplication application = UserValidationUtils.validateAdminApplicationStatus(
                    adminApplicationMapper, applicationId, "PENDING");

            application.setStatus("REJECTED");
            application.setReason(reason != null ? reason : "申请不符合要求");
            application.setProcessorId(currentDev.getId());
            application.setProcessedAt(LocalDateTime.now());
            adminApplicationMapper.update(application);

            return RestResponse.success("申请已拒绝");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "拒绝申请失败: " + ex.getMessage());
        }
    }
}