package com.github.konstantyn111.crashapi.controller.admin;

import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.service.admin.AdminService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员管理", description = "系统管理员操作接口")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "获取用户信息",
            description = "根据用户ID获取用户详细信息",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "成功获取用户信息",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfo.class))),
                    @ApiResponse(responseCode = "404", description = "用户不存在")
            }
    )
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_DEV', 'ROLE_ADMIN')")
    public RestResponse<UserInfo> getUserById(
            @Parameter(name = "userId", description = "用户ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long userId) {
        return adminService.getUserInfoById(userId);
    }

    @Operation(
            summary = "更新用户角色",
            description = "更新用户的系统角色",
            responses = {
                    @ApiResponse(responseCode = "200", description = "角色更新成功"),
                    @ApiResponse(responseCode = "400", description = "角色不存在"),
                    @ApiResponse(responseCode = "404", description = "用户不存在")
            }
    )
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasAnyRole('ROLE_DEV', 'ROLE_ADMIN')")
    public RestResponse<Void> updateUserRole(
            @Parameter(name = "userId", description = "用户ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long userId,
            @Parameter(name = "role", description = "角色名称（如：ROLE_ADMIN）", required = true, in = ParameterIn.QUERY)
            @RequestParam String role) {
        return adminService.updateUserRole(userId, role);
    }
}