package com.github.konstantyn111.crashapi.controller;

import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.service.UserService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public RestResponse<UserInfo> getCurrentUser() {
        return userService.getCurrentUserInfo();
    }

    @PatchMapping("/me")
    public RestResponse<UserInfo> updateUser(@RequestBody UserInfo updateData) {
        return userService.updateUserInfo(updateData);
    }

    @PostMapping("/me/password")
    public RestResponse<Void> updatePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        return userService.updatePassword(oldPassword, newPassword);
    }

    @PostMapping("/me/avatar")
    public RestResponse<String> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        return userService.updateAvatar(file);
    }

    /**
     * 申请成为管理员
     * @param reason 申请理由
     * @return 操作结果
     */
    @PostMapping("/apply-for-admin")
    public RestResponse<Void> applyForAdmin(@RequestParam("reason") String reason) {
        return userService.applyForAdminRole(reason);
    }
}