package com.github.konstantyn111.crashapi.controller.user;

import com.github.konstantyn111.crashapi.dto.user.AdminApplicationDTO;
import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.service.user.DeveloperService;
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
     * 获取所有用户分页列表
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @GetMapping("/users")
    public ResponseEntity<RestResponse<Page<UserInfo>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(developerService.getAllUsers(pageable));
    }

    /**
     * 提升用户为管理员
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/users/{userId}/promote")
    public ResponseEntity<RestResponse<Void>> promoteToAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok(developerService.promoteToAdmin(userId));
    }

    /**
     * 撤销用户管理员权限
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/users/{userId}/revoke-admin")
    public ResponseEntity<RestResponse<Void>> revokeAdminRole(@PathVariable Long userId) {
        return ResponseEntity.ok(developerService.revokeAdminRole(userId));
    }

    /**
     * 获取待处理管理员申请
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
            @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(developerService.rejectApplication(applicationId, feedback));
    }
}