package com.github.konstantyn111.crashapi.controller;

import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.service.UserService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "用户管理", description = "普通用户操作接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "获取当前用户信息",
            description = "获取当前登录用户的详细信息",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "成功获取用户信息",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfo.class)
                            )
                    )
            }
    )
    @GetMapping("/me")
    public RestResponse<UserInfo> getCurrentUser() {
        return userService.getCurrentUserInfo();
    }

    @Operation(
            summary = "更新用户信息",
            description = "更新当前登录用户的基本信息",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "用户信息更新成功",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfo.class))),
                    @ApiResponse(responseCode = "400", description = "邮箱已被使用")
            }
    )
    @PatchMapping("/me")
    public RestResponse<UserInfo> updateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "用户更新数据",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserInfo.class)
                    )
            )
            @RequestBody UserInfo updateData) {
        return userService.updateUserInfo(updateData);
    }

    @Operation(
            summary = "修改密码",
            description = "修改当前登录用户的密码",
            responses = {
                    @ApiResponse(responseCode = "200", description = "密码更新成功"),
                    @ApiResponse(responseCode = "401", description = "旧密码不正确")
            }
    )
    @PostMapping("/me/password")
    public RestResponse<Void> updatePassword(
            @Parameter(name = "oldPassword", description = "旧密码", required = true, in = ParameterIn.QUERY)
            @RequestParam("oldPassword") String oldPassword,
            @Parameter(name = "newPassword", description = "新密码", required = true, in = ParameterIn.QUERY)
            @RequestParam("newPassword") String newPassword) {
        return userService.updatePassword(oldPassword, newPassword);
    }

    @Operation(
            summary = "更新头像",
            description = "上传并更新当前用户的头像",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "头像更新成功",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "无效的文件类型")
            }
    )
    @PostMapping("/me/avatar")
    public RestResponse<String> updateAvatar(
            @Parameter(
                    name = "avatar",
                    description = "头像图片文件",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestParam("avatar") MultipartFile file) {
        return userService.updateAvatar(file);
    }

    @Operation(
            summary = "申请管理员权限",
            description = "提交管理员权限申请",
            responses = {
                    @ApiResponse(responseCode = "200", description = "申请提交成功"),
                    @ApiResponse(responseCode = "400", description = "已是管理员或已有待处理申请")
            }
    )
    @PostMapping("/apply-for-admin")
    public RestResponse<Void> applyForAdmin(
            @Parameter(name = "reason", description = "申请理由", required = true, in = ParameterIn.QUERY)
            @RequestParam("reason") String reason) {
        return userService.applyForAdminRole(reason);
    }
}