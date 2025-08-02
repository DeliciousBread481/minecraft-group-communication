package com.github.konstantyn111.crashapi.controller.admin;

import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.dto.user.AdminApplicationDTO;
import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.service.admin.DeveloperService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/developer")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    /**
     * 分页获取所有用户信息
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @GetMapping("/users")
    public ResponseEntity<RestResponse<Page<UserInfo>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(developerService.getAllUsers(pageable));
    }

    /**
     * 提升用户权限
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/users/{userId}/promote")
    public ResponseEntity<RestResponse<Void>> promoteToAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok(developerService.promoteToAdmin(userId));
    }

    /**
     * 撤销用户的管理员权限
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/users/{userId}/revoke-admin")
    public ResponseEntity<RestResponse<Void>> revokeAdminRole(@PathVariable Long userId) {
        return ResponseEntity.ok(developerService.revokeAdminRole(userId));
    }

    /**
     * 分页获取未处理的管理员申请
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @GetMapping("/admin-applications/pending")
    public ResponseEntity<RestResponse<Page<AdminApplicationDTO>>> getPendingApplications(Pageable pageable) {
        return ResponseEntity.ok(developerService.getPendingApplications(pageable));
    }

    /**
     * 批准管理员申请
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/admin-applications/{applicationId}/approve")
    public ResponseEntity<RestResponse<Void>> approveApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(developerService.approveApplication(applicationId));
    }

    /**
     * 拒绝管理员申请
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/admin-applications/{applicationId}/reject")
    public ResponseEntity<RestResponse<Void>> rejectApplication(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(developerService.rejectApplication(applicationId, reason));
    }

    /**
     * 分页获取未审核的解决方案
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @GetMapping("/solutions/pending")
    public ResponseEntity<RestResponse<Page<SolutionDTO>>> getPendingSolutions(Pageable pageable) {
        return ResponseEntity.ok(developerService.getPendingSolutions(pageable));
    }

    /**
     * 批准解决方案发布
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/solutions/{solutionId}/approve")
    public ResponseEntity<RestResponse<Void>> approveSolution(@PathVariable String solutionId) {
        return ResponseEntity.ok(developerService.approveSolution(solutionId));
    }

    /**
     * 拒绝解决方案发布
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/solutions/{solutionId}/reject")
    public ResponseEntity<RestResponse<Void>> rejectSolution(
            @PathVariable String solutionId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(developerService.rejectSolution(solutionId, reason));
    }

    /**
     * 更新已发布的解决方案
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PutMapping("/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> updateSolution(
            @PathVariable String solutionId,
            @RequestBody SolutionUpdateDTO updateDTO) {
        return ResponseEntity.ok(developerService.updateSolution(solutionId, updateDTO));
    }
}