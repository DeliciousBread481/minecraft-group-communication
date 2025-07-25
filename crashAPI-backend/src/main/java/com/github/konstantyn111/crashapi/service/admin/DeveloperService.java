package com.github.konstantyn111.crashapi.service.admin;

import com.github.konstantyn111.crashapi.dto.AdminApplicationDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.entity.*;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.AdminApplicationMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.mapper.UserMapper;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.ErrorCode;
import com.github.konstantyn111.crashapi.util.SolutionConvertUtil;
import com.github.konstantyn111.crashapi.util.UserConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final SolutionMapper solutionMapper;
    private final UserMapper userMapper;
    private final AdminApplicationMapper adminApplicationMapper;

    // 开发者角色常量
    private static final String DEVELOPER_ROLE = "ROLE_DEV";
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    /**
     * 获取所有用户信息（仅开发者）
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
     * 将普通用户提升为管理员（仅开发者）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> promoteToAdmin(Long userId) {
        try {
            // 验证开发者权限
            validateDeveloperPermissions();

            User user = userMapper.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            // 检查目标用户是否已是管理员
            if (user.getRoles().stream().anyMatch(role -> ADMIN_ROLE.equals(role.getName()))) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "用户已是管理员");
            }

            Role adminRole = userMapper.findRoleByName(ADMIN_ROLE)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND,
                            HttpStatus.BAD_REQUEST,
                            "管理员角色不存在"));

            // 添加管理员角色（保留现有角色）
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
     * 撤销管理员权限（仅开发者）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> revokeAdminRole(Long userId) {
        try {
            // 验证开发者权限
            validateDeveloperPermissions();

            User user = userMapper.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            // 检查目标用户是否确实是管理员
            Optional<Role> adminRole = user.getRoles().stream()
                    .filter(role -> ADMIN_ROLE.equals(role.getName()))
                    .findFirst();

            if (adminRole.isEmpty()) {
                throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
                        HttpStatus.BAD_REQUEST,
                        "用户不是管理员");
            }

            // 移除管理员角色
            userMapper.removeRoleFromUser(userId, adminRole.get().getId());

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
     * 获取待处理的管理员申请（仅开发者）
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
     * 批准管理员申请（仅开发者）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> approveApplication(Long applicationId) {
        try {
            // 验证开发者权限
            User currentDev = validateDeveloperPermissions();

            AdminApplication application = adminApplicationMapper.findByIdWithUserInfo(applicationId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "申请记录不存在"));

            if (!"PENDING".equals(application.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_APPLICATION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "申请状态不可操作");
            }

            // 添加管理员角色
            Role adminRole = userMapper.findRoleByName(ADMIN_ROLE)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND,
                            HttpStatus.BAD_REQUEST,
                            "管理员角色不存在"));

            // 检查是否已有管理员角色
            if (application.getUser().getRoles().stream()
                    .anyMatch(role -> ADMIN_ROLE.equals(role.getName()))) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "用户已是管理员");
            }

            userMapper.addRoleToUser(application.getUserId(), adminRole.getId());

            // 更新申请状态
            application.setStatus("APPROVED");
            application.setProcessorId(currentDev.getId());
            application.setProcessor(currentDev);
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
     * 拒绝管理员申请（仅开发者）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> rejectApplication(Long applicationId, String reason) {
        try {
            // 验证开发者权限
            User currentDev = validateDeveloperPermissions();

            AdminApplication application = adminApplicationMapper.findById(applicationId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "申请记录不存在"));

            if (!"PENDING".equals(application.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_APPLICATION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "申请状态不可操作");
            }

            // 更新申请状态
            application.setStatus("REJECTED");
            application.setReason(reason != null ? reason : "申请不符合要求");
            application.setProcessorId(currentDev.getId());
            application.setProcessor(currentDev);
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

    /**
     * 验证当前用户是否为开发者并返回用户实体
     */
    private User validateDeveloperPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "当前用户不存在"));

        // 检查开发者权限
        boolean isDeveloper = currentUser.getRoles().stream()
                .anyMatch(role -> DEVELOPER_ROLE.equals(role.getName()));

        if (!isDeveloper) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                    HttpStatus.FORBIDDEN,
                    "只有开发者能执行此操作");
        }

        return currentUser;
    }

    /**
     * 获取待审核的解决方案
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getPendingSolutions(Pageable pageable) {
        try {
            List<Solution> solutions = solutionMapper.findByStatus(
                    "待审核",
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );

            long total = solutionMapper.countByStatus("待审核");

            // 使用SolutionConvertUtil转换解决方案
            List<SolutionDTO> dtos = solutions.stream()
                    .map(SolutionConvertUtil::convertToSolutionDTO)
                    .collect(Collectors.toList());

            Page<SolutionDTO> page = new PageImpl<>(dtos, pageable, total);
            return RestResponse.success(page, "获取待审核解决方案成功");
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取待审核解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 批准解决方案
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> approveSolution(String solutionId) {
        try {
            // 验证开发者权限
            User currentDev = validateDeveloperPermissions();

            Solution solution = solutionMapper.findById(solutionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "解决方案不存在"));

            // 验证状态（必须是待审核状态）
            if (!"待审核".equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "解决方案状态不可操作");
            }

            // 更新状态为已发布
            solution.setStatus("已发布");
            solution.setReviewedBy(currentDev.getId());
            solution.setUpdatedAt(LocalDateTime.now());
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已批准发布");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.SOLUTION_REVIEW_FAILED,
                    "批准解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 拒绝解决方案
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> rejectSolution(String solutionId, String reason) {
        try {
            // 验证开发者权限
            User currentDev = validateDeveloperPermissions();

            Solution solution = solutionMapper.findById(solutionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "解决方案不存在"));

            // 验证状态（必须是待审核状态）
            if (!"待审核".equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "解决方案状态不可操作");
            }

            // 更新状态为草稿并添加拒绝理由
            solution.setStatus("草稿");
            solution.setNotes("审核拒绝原因: " + reason);
            solution.setReviewedBy(currentDev.getId());
            solution.setUpdatedAt(LocalDateTime.now());
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已拒绝");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.SOLUTION_REVIEW_FAILED,
                    "拒绝解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 添加开发者角色（仅超级管理员使用，非API）
     */
    @Transactional
    protected void addDeveloperRole(Long userId) {
        Role devRole = userMapper.findRoleByName(DEVELOPER_ROLE)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND,
                        HttpStatus.BAD_REQUEST,
                        "开发者角色不存在"));
        userMapper.addRoleToUser(userId, devRole.getId());
    }
}