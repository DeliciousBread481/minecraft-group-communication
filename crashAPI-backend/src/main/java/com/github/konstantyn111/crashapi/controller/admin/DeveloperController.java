package com.github.konstantyn111.crashapi.controller.admin;

import com.github.konstantyn111.crashapi.dto.*;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.service.admin.DeveloperService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/developer")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    /**
     * 获取所有用户信息
     * @param pageable 分页参数
     * @return 用户信息分页响应
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Page<UserInfo>> getAllUsers(Pageable pageable) {
        return developerService.getAllUsers(pageable);
    }

    /**
     * 将普通用户提升为管理员
     * @param userId 目标用户ID
     * @return 操作结果
     */
    @PutMapping("/users/{userId}/promote-to-admin")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Void> promoteToAdmin(@PathVariable Long userId) {
        return developerService.promoteToAdmin(userId);
    }

    /**
     * 撤销用户的管理员权限
     * @param userId 目标用户ID
     * @return 操作结果
     */
    @PutMapping("/users/{userId}/revoke-admin")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Void> revokeAdminRole(@PathVariable Long userId) {
        return developerService.revokeAdminRole(userId);
    }

    /**
     * 获取待处理的管理员申请列表
     * @param pageable 分页参数
     * @return 申请列表
     */
    @GetMapping("/admin-applications/pending")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Page<AdminApplicationDTO>> getPendingApplications(Pageable pageable) {
        return developerService.getPendingApplications(pageable);
    }

    /**
     * 批准管理员申请
     * @param applicationId 申请ID
     * @return 操作结果
     */
    @PutMapping("/admin-applications/{applicationId}/approve")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Void> approveAdminApplication(@PathVariable Long applicationId) {
        return developerService.approveApplication(applicationId);
    }

    /**
     * 拒绝管理员申请
     * @param applicationId 申请ID
     * @param reason 拒绝理由
     * @return 操作结果
     */
    @PutMapping("/admin-applications/{applicationId}/reject")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Void> rejectAdminApplication(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String reason) {
        return developerService.rejectApplication(applicationId, reason);
    }

    /**
     * 获取待审核的解决方案
     * @param pageable 分页参数
     * @return 解决方案列表
     */
    @GetMapping("/solutions/pending")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Page<SolutionDTO>> getPendingSolutions(Pageable pageable) {
        return developerService.getPendingSolutions(pageable);
    }

    /**
     * 批准解决方案
     * @param solutionId 解决方案ID
     * @return 操作结果
     */
    @PutMapping("/solutions/{solutionId}/approve")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Void> approveSolution(@PathVariable String solutionId) {
        return developerService.approveSolution(solutionId);
    }

    /**
     * 拒绝解决方案
     * @param solutionId 解决方案ID
     * @param reason 拒绝理由
     * @return 操作结果
     */
    @PutMapping("/solutions/{solutionId}/reject")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Void> rejectSolution(
            @PathVariable String solutionId,
            @RequestParam String reason) {
        return developerService.rejectSolution(solutionId, reason);
    }
}