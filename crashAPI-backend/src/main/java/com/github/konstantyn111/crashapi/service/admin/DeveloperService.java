package com.github.konstantyn111.crashapi.service.admin;

import com.github.konstantyn111.crashapi.dto.AdminApplicationDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.entity.*;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.AdminApplicationMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionImageMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.mapper.UserMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionStepMapper;
import com.github.konstantyn111.crashapi.util.*;
import com.github.konstantyn111.crashapi.util.admin.*;
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
    private final SolutionMapper solutionMapper;
    private final SolutionStepMapper solutionStepMapper;
    private final SolutionImageMapper solutionImageMapper;
    private final AdminApplicationMapper adminApplicationMapper;

    /**
     * <p> ---- 开发者接口 ---- <p/>
     * 分页获取所有用户信息
     * @param pageable 分页参数
     * @return 用户信息分页数据
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
     * <p> ---- 开发者接口 ---- <p/>
     * 提升用户权限
     * @param userId 用户ID
     * @return API响应结果
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> promoteToAdmin(Long userId) {
        try {
            User currentDev = SecurityUtils.validateDeveloperPermissions(userMapper);
            User targetUser = ValidationUtils.validateUserExists(userMapper, userId, "提升权限-");

            if (RoleUtils.hasRole(targetUser, RoleUtils.ADMIN_ROLE)) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "用户已是管理员");
            }

            Role adminRole = RoleUtils.getAdminRole(userMapper);
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
     * <p> ---- 开发者接口 ---- <p/>
     *  撤销用户的管理员权限
     * @param userId 用户ID
     * @return API响应结果
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> revokeAdminRole(Long userId) {
        try {
            SecurityUtils.validateDeveloperPermissions(userMapper);
            User targetUser = ValidationUtils.validateUserExists(userMapper, userId, "撤销权限-");

            if (!RoleUtils.hasRole(targetUser, RoleUtils.ADMIN_ROLE)) {
                throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
                        HttpStatus.BAD_REQUEST,
                        "用户不是管理员");
            }

            Role adminRole = RoleUtils.getAdminRole(userMapper);
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
     * <p> ---- 开发者接口 ---- <p/>
     * 分页获取未处理的管理员申请
     * @param pageable 分页参数
     * @return 管理员申请的分页数据
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
     * <p> ---- 开发者接口 ---- <p/>
     * 批准管理员申请
     * @param applicationId 申请记录ID
     * @return API响应结果
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> approveApplication(Long applicationId) {
        try {
            User currentDev = SecurityUtils.validateDeveloperPermissions(userMapper);
            AdminApplication application = ValidationUtils.validateAdminApplicationStatus(
                    adminApplicationMapper, applicationId, "PENDING");

            User targetUser = ValidationUtils.validateUserExists(userMapper,
                    application.getUserId(), "申请关联的");

            if (RoleUtils.hasRole(targetUser, RoleUtils.ADMIN_ROLE)) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "用户已是管理员");
            }

            Role adminRole = RoleUtils.getAdminRole(userMapper);
            userMapper.addRoleToUser(application.getUserId(), adminRole.getId());

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
     * <p> ---- 开发者接口 ---- <p/>
     * 拒绝管理员申请
     * @param applicationId 申请记录ID
     * @param reason 拒绝原因
     * @return API响应结果
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> rejectApplication(Long applicationId, String reason) {
        try {
            User currentDev = SecurityUtils.validateDeveloperPermissions(userMapper);
            AdminApplication application = ValidationUtils.validateAdminApplicationStatus(
                    adminApplicationMapper, applicationId, "PENDING");

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
     * <p> ---- 开发者接口 ---- <p/>
     * 分页获取未审核的解决方案
     * @param pageable 分页数据
     * @return 解决方案分页数据
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getPendingSolutions(Pageable pageable) {
        try {
            List<Solution> solutions = solutionMapper.findByStatus(
                    SolutionStatus.PENDING_REVIEW,
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );

            long total = solutionMapper.countByStatus(SolutionStatus.PENDING_REVIEW);

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
     * <p> ---- 开发者接口 ---- <p/>
     * 批准解决方案发布
     * @param solutionId 解决方案ID
     * @return API响应结果
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> approveSolution(String solutionId) {
        try {
            User currentDev = SecurityUtils.validateDeveloperPermissions(userMapper);
            Solution solution = ValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            SolutionStateUtils.transitionState(solution, SolutionStateUtils.SolutionStateAction.APPROVE, null);
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
     * <p> ---- 开发者接口 ---- <p/>
     * 拒绝解决方案发布
     * @param solutionId 解决方案ID
     * @param reason 拒绝原因
     * @return API响应结果
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<Void> rejectSolution(String solutionId, String reason) {
        try {
            User currentDev = SecurityUtils.validateDeveloperPermissions(userMapper);
            Solution solution = ValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            SolutionStateUtils.transitionState(solution, SolutionStateUtils.SolutionStateAction.REJECT, reason);
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
     * <p> ---- 开发者接口 ---- <p/>
     * 更新已发布的解决方案
     * @param solutionId 解决方案ID
     * @param updateDTO 更新数据
     * @return 更新后的解决方案DTO/API响应结果
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @Transactional
    public RestResponse<SolutionDTO> updateSolution(String solutionId, SolutionUpdateDTO updateDTO) {
        try {
            SecurityUtils.validateDeveloperPermissions(userMapper);
            Solution solution = ValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            if (!SolutionStatus.PUBLISHED.equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "只能修改已发布状态的解决方案");
            }

            SolutionUpdateUtil.updateSolutionCore(
                    solution,
                    updateDTO,
                    solutionMapper,
                    solutionStepMapper,
                    solutionImageMapper
            );
            SolutionDTO dto = SolutionConvertUtil.convertToSolutionDTO(solution);
            return RestResponse.success(dto, "解决方案更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新解决方案失败: " + ex.getMessage());
        }
    }
}