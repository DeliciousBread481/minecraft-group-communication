package com.github.konstantyn111.crashapi.controller.user;

import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.service.user.UserService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public RestResponse<UserInfo> getCurrentUser() {
        return userService.getCurrentUserInfo();
    }

    /**
     * 更新当前用户基本信息（昵称或邮箱）
     */
    @PatchMapping("/me")
    public RestResponse<UserInfo> updateUser(@RequestBody UserInfo updateData) {
        return userService.updateUserInfo(updateData);
    }

    /**
     * 修改当前用户密码
     */
    @PostMapping("/me/password")
    public RestResponse<Void> updatePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        return userService.updatePassword(oldPassword, newPassword);
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/me/avatar")
    public RestResponse<String> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        return userService.updateAvatar(file);
    }

    /**
     * 提交管理员权限申请
     */
    @PostMapping("/apply-for-admin")
    public RestResponse<Void> applyForAdmin(@RequestParam("reason") String reason) {
        return userService.applyForAdminRole(reason);
    }
}