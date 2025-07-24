package com.github.konstantyn111.crashapi.service.admin;

import com.github.konstantyn111.crashapi.dto.AdminApplicationDTO;
import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.entity.AdminApplication;
import com.github.konstantyn111.crashapi.entity.Role;
import com.github.konstantyn111.crashapi.entity.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.AdminApplicationMapper;
import com.github.konstantyn111.crashapi.mapper.UserMapper;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.ErrorCode;
import com.github.konstantyn111.crashapi.util.UserConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final UserMapper userMapper;
    private final AdminApplicationMapper adminApplicationMapper;
    private final AdminService adminService;

    /**
     * 获取所有用户信息
     */
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
     * 将普通用户提升为管理员
     */
    @Transactional
    public RestResponse<Void> promoteToAdmin(Long userId) {
        try {
            User user = userMapper.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            Role adminRole = userMapper.findRoleByName("ROLE_ADMIN")
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND,
                            HttpStatus.BAD_REQUEST,
                            "管理员角色不存在"));

            userMapper.deleteUserRolesByUserId(userId);
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
     * 获取待处理的管理员申请
     */
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
     * 批准管理员申请
     */
    @Transactional
    public RestResponse<Void> approveApplication(Long applicationId) {
        try {
            // 获取当前处理的管理员ID
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User processor = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "处理人不存在"));

            AdminApplication application = adminApplicationMapper.findByIdWithUserInfo(applicationId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "申请记录不存在"));

            if (!"PENDING".equals(application.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_APPLICATION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "申请状态不可操作");
            }

            // 使用AdminService提升用户权限
            RestResponse<Void> result = adminService.updateUserRole(
                    application.getUserId(),
                    "ROLE_ADMIN"
            );

            if (!result.isSuccess()) {
                throw new BusinessException(ErrorCode.OPERATION_FAILED,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "提升用户为管理员失败: " + result.getMessage());
            }

            // 更新申请状态和处理人信息
            application.setStatus("APPROVED");
            application.setProcessorId(processor.getId());
            application.setProcessor(processor); // 设置处理人对象
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
     * 拒绝管理员申请
     */
    @Transactional
    public RestResponse<Void> rejectApplication(Long applicationId, String reason) {
        try {
            // 获取当前处理的管理员ID
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User processor = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "处理人不存在"));

            AdminApplication application = adminApplicationMapper.findById(applicationId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "申请记录不存在"));

            if (!"PENDING".equals(application.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_APPLICATION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "申请状态不可操作");
            }

            // 更新申请状态和处理人信息
            application.setStatus("REJECTED");
            application.setReason(reason);
            application.setProcessorId(processor.getId());
            application.setProcessor(processor);
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