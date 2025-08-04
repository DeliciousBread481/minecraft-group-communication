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

/**
 * 开发者管理控制器
 * <p>
 * 提供开发者权限管理接口，包括用户管理、权限变更和申请审批功能。
 * 所有接口都需要ROLE_DEV权限才能访问。
 * </p>
 */
@RestController
@RequestMapping("/api/developer")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    /**
     * 分页获取所有用户信息
     * <p>
     * 查询包含角色信息的用户列表，结果按分页参数返回。
     * </p>
     *
     * @param pageable 分页参数（页大小、页码等）
     * @return 分页的用户信息列表响应实体
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @GetMapping("/users")
    public ResponseEntity<RestResponse<Page<UserInfo>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(developerService.getAllUsers(pageable));
    }

    /**
     * 提升用户为管理员
     * <p>
     * 为目标用户添加管理员角色。若用户已是管理员将抛出异常。
     * </p>
     *
     * @param userId 要提升权限的用户ID
     * @return 操作结果响应（无数据体）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/users/{userId}/promote")
    public ResponseEntity<RestResponse<Void>> promoteToAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok(developerService.promoteToAdmin(userId));
    }

    /**
     * 撤销用户的管理员权限
     * <p>
     * 移除目标用户的管理员角色。若用户不是管理员将抛出异常。
     * </p>
     *
     * @param userId 要撤销权限的用户ID
     * @return 操作结果响应（无数据体）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/users/{userId}/revoke-admin")
    public ResponseEntity<RestResponse<Void>> revokeAdminRole(@PathVariable Long userId) {
        return ResponseEntity.ok(developerService.revokeAdminRole(userId));
    }

    /**
     * 分页获取待处理的管理员申请
     * <p>
     * 查询状态为PENDING的申请记录，包含关联的用户信息。
     * </p>
     *
     * @param pageable 分页参数
     * @return 分页的申请信息列表响应实体
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @GetMapping("/admin-applications/pending")
    public ResponseEntity<RestResponse<Page<AdminApplicationDTO>>> getPendingApplications(Pageable pageable) {
        return ResponseEntity.ok(developerService.getPendingApplications(pageable));
    }

    /**
     * 批准管理员申请
     * <p>
     * 将申请状态改为APPROVED并为关联用户添加管理员角色。
     * </p>
     *
     * @param applicationId 要批准的申请ID
     * @return 操作结果响应（无数据体）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/admin-applications/{applicationId}/approve")
    public ResponseEntity<RestResponse<Void>> approveApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(developerService.approveApplication(applicationId));
    }

    /**
     * 拒绝管理员申请
     * <p>
     * 将申请状态改为REJECTED，可附加拒绝原因。
     * </p>
     *
     * @param applicationId 要拒绝的申请ID
     * @param reason 拒绝原因（可选）
     * @return 操作结果响应（无数据体）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/admin-applications/{applicationId}/reject")
    public ResponseEntity<RestResponse<Void>> rejectApplication(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(developerService.rejectApplication(applicationId, reason));
    }
}