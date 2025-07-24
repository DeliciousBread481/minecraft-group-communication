package com.github.konstantyn111.crashapi.controller.admin;

import com.github.konstantyn111.crashapi.dto.AdminApplicationDTO;
import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.service.admin.DeveloperService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "开发者管理", description = "开发者专用管理接口")
@RestController
@RequestMapping("/api/developer")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    @Operation(
            summary = "获取所有用户",
            description = "获取系统所有用户的分页列表（开发者专用）",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "成功获取用户列表",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class)
                            )
                    )
            }
    )
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Page<UserInfo>> getAllUsers(
            @ParameterObject
            Pageable pageable) {
        return developerService.getAllUsers(pageable);
    }

    @Operation(
            summary = "提升用户为管理员",
            description = "直接将用户提升为系统管理员（开发者专用）",
            responses = {
                    @ApiResponse(responseCode = "200", description = "用户权限提升成功"),
                    @ApiResponse(responseCode = "404", description = "用户不存在")
            }
    )
    @PutMapping("/users/{userId}/promote-to-admin")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Void> promoteToAdmin(
            @Parameter(name = "userId", description = "用户ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long userId) {
        return developerService.promoteToAdmin(userId);
    }

    @Operation(
            summary = "获取待处理申请",
            description = "获取所有待处理的管理员申请（开发者专用）",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "成功获取申请列表",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class)))
            }
    )
    @GetMapping("/admin-applications/pending")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Page<AdminApplicationDTO>> getPendingApplications(
            @ParameterObject // 替代 @ApiIgnore
            Pageable pageable) {
        return developerService.getPendingApplications(pageable);
    }

    @Operation(
            summary = "批准管理员申请",
            description = "批准用户的管理员权限申请（开发者专用）",
            responses = {
                    @ApiResponse(responseCode = "200", description = "申请已批准"),
                    @ApiResponse(responseCode = "400", description = "申请状态无效"),
                    @ApiResponse(responseCode = "404", description = "申请不存在")
            }
    )
    @PutMapping("/admin-applications/{applicationId}/approve")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Void> approveAdminApplication(
            @Parameter(name = "applicationId", description = "申请ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long applicationId) {
        return developerService.approveApplication(applicationId);
    }

    @Operation(
            summary = "拒绝管理员申请",
            description = "拒绝用户的管理员权限申请（开发者专用）",
            responses = {
                    @ApiResponse(responseCode = "200", description = "申请已拒绝"),
                    @ApiResponse(responseCode = "400", description = "申请状态无效"),
                    @ApiResponse(responseCode = "404", description = "申请不存在")
            }
    )
    @PutMapping("/admin-applications/{applicationId}/reject")
    @PreAuthorize("hasRole('ROLE_DEV')")
    public RestResponse<Void> rejectAdminApplication(
            @Parameter(name = "applicationId", description = "申请ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long applicationId,
            @Parameter(name = "reason", description = "拒绝理由", required = true, in = ParameterIn.QUERY)
            @RequestParam("reason") String reason) {
        return developerService.rejectApplication(applicationId, reason);
    }
}