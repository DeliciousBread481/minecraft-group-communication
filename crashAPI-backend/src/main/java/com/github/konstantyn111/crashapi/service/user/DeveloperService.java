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
import com.github.konstantyn111.crashapi.util.user.UserUtils;
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
import java.util.Set;
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
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional(readOnly = true)
    public RestResponse<Page<UserInfo>> getAllUsers(Pageable pageable) {
        try {
            List<User> users = userMapper.findAllWithRolesPaged(
                    (int) pageable.getOffset(),
                    pageable.getPageSize()
            );
            
            List<UserInfo> userInfos = users.stream().map(user -> {
                Set<String> roles = userMapper.findRolesByUserId(user.getId());
                return UserUtils.Convert.toUserInfo(user, roles);
            }).collect(Collectors.toList());
            
            if (users.isEmpty()) {
                log.warn("No users found");
                return RestResponse.success(Page.empty(), "获取用户列表成功-NO CONTENT");
            }
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
     * 将用户提升为管理员
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> promoteToAdmin(Long userId) {
        try {
            User currentDev = UserUtils.Security.validateDeveloperPermissions(userMapper);
            UserUtils.Validation.validateUserExists(userMapper, userId, "提升权限-");

            if (userMapper.hasRole(userId, UserUtils.RoleUtils.ADMIN_ROLE)) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "用户已是管理员");
            }

            Role adminRole = UserUtils.RoleUtils.getAdminRole(roleMapper);
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
     * 撤销用户的管理员权限
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> revokeAdminRole(Long userId) {
        try {
            UserUtils.Security.validateDeveloperPermissions(userMapper);
            UserUtils.Validation.validateUserExists(userMapper, userId, "撤销权限-");

            if (!userMapper.hasRole(userId, UserUtils.RoleUtils.ADMIN_ROLE)) {
                throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
                        HttpStatus.BAD_REQUEST,
                        "用户不是管理员");
            }

            Role adminRole = UserUtils.RoleUtils.getAdminRole(roleMapper);
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
     * 获取待处理的管理员申请列表
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional(readOnly = true)
    public RestResponse<Page<AdminApplicationDTO>> getPendingApplications(Pageable pageable) {
        try {
            List<AdminApplication> applications = adminApplicationMapper
                    .findPendingApplicationsWithUserInfo( (int) pageable.getOffset(),pageable.getPageSize());
            
            long total = adminApplicationMapper.countPendingApplications();
            log.info("待处理申请总数: {}", total);
            
            List<AdminApplicationDTO> dtos = applications.stream()
                    .map(UserUtils.Convert::toAdminAppDTO)
                    .collect(Collectors.toList());
            
            if (applications.isEmpty()) {
                log.warn("未找到待处理的申请");
                return RestResponse.success(Page.empty(), "获取申请列表成功-NO CONTENT");
            }
            Page<AdminApplicationDTO> page = new PageImpl<>(dtos, pageable, total);
            return RestResponse.success(page, "获取申请列表成功");
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取申请列表失败: " + ex.getMessage());
        }
    }

    /**
     * 批准管理员申请
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> approveApplication(Long applicationId) {
        try {
            User currentDev = UserUtils.Security.validateDeveloperPermissions(userMapper);

            AdminApplication application = UserUtils.Validation.validateAdminApplicationStatus(
                    adminApplicationMapper, applicationId, "PENDING");

            User targetUser = userMapper.findById(application.getApplicantId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND, "申请关联的用户不存在"));

            if (userMapper.hasRole(targetUser.getId(), UserUtils.RoleUtils.ADMIN_ROLE)) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "用户已是管理员");
            }

            Role adminRole = UserUtils.RoleUtils.getAdminRole(roleMapper);
            userMapper.addRoleToUser(application.getApplicantId(), adminRole.getId());

            application.setStatus("APPROVED");
            application.setProcessorId(currentDev.getId());
            application.setProcessedAt(LocalDateTime.now());
            application.setFeedback("申请已通过，无更多消息");

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
     * 拒绝管理员申请
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> rejectApplication(Long applicationId, String feedback) {
        try {
            User currentDev = UserUtils.Security.validateDeveloperPermissions(userMapper);

            AdminApplication application = UserUtils.Validation.validateAdminApplicationStatus(
                    adminApplicationMapper, applicationId, "PENDING");

            application.setStatus("REJECTED");
            application.setProcessorId(currentDev.getId());
            application.setProcessedAt(LocalDateTime.now());
            application.setFeedback(feedback != null ? feedback : "申请不符合要求");

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
