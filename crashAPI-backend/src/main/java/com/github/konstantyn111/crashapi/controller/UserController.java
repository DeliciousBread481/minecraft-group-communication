package com.github.konstantyn111.crashapi.controller;

import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.service.UserService;
import com.github.konstantyn111.crashapi.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.github.konstantyn111.crashapi.util.ErrorCode;

/**
 * 用户信息控制器
 * 处理当前用户信息管理及管理员用户操作
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户信息
     * @return 包含用户详细信息的响应
     */
    @GetMapping("/me")
    public ApiResponse<UserInfo> getCurrentUser() {
        return userService.getCurrentUserInfo();
    }

    /**
     * 更新当前用户信息
     * @param updateData 待更新的用户数据
     * @return 更新后的用户信息
     */
    @PatchMapping("/me")
    public ApiResponse<UserInfo> updateUser(@RequestBody UserInfo updateData) {
        return userService.updateUserInfo(updateData);
    }

    /**
     * 修改当前用户密码
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 操作结果（无数据返回）
     */
    @PostMapping("/me/password")
    public ApiResponse<Void> updatePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        return userService.updatePassword(oldPassword, newPassword);
    }

    /**
     * 更新用户头像
     * @param file 上传的头像文件
     * @return 包含新头像URL的响应
     */
    @PostMapping("/me/avatar")
    public ApiResponse<String> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        return userService.updateAvatar(file);
    }

    // ============= 管理员功能（尚未实现） =============

    /**
     * 根据ID获取用户信息（管理员）
     * @param userId 目标用户ID
     * @return 未实现的功能提示
     */
    @GetMapping("/{userId}")
    public ApiResponse<UserInfo> getUserById(@PathVariable Long userId) {
        return ApiResponse.fail(HttpStatus.NOT_IMPLEMENTED.value(),
                ErrorCode.FEATURE_NOT_IMPLEMENTED,
                "功能尚未实现");
    }

    /**
     * 更新用户角色（管理员）
     * @param userId 目标用户ID
     * @param role 新角色标识
     * @return 未实现的功能提示
     */
    @PutMapping("/{userId}/role")
    public ApiResponse<Void> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        return ApiResponse.fail(HttpStatus.NOT_IMPLEMENTED.value(),
                ErrorCode.FEATURE_NOT_IMPLEMENTED,
                "功能尚未实现");
    }
}