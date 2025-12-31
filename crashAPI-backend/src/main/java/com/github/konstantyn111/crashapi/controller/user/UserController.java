package com.github.konstantyn111.crashapi.controller.user;

import com.github.konstantyn111.crashapi.controller.BaseController;
import com.github.konstantyn111.crashapi.dto.user.AdminApplicationStatus;
import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.service.user.UserService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<RestResponse<UserInfo>> getCurrentUser() {
        return success(userService.getCurrentUserInfo());
    }

    /**
     * 更新当前用户基本信息（昵称或邮箱）
     */
    @PatchMapping("/me")
    public ResponseEntity<RestResponse<UserInfo>> updateUser(@RequestBody UserInfo updateData) {
        return success(userService.updateUserInfo(updateData));
    }

    /**
     * 修改当前用户密码
     */
    @PostMapping("/me/password")
    public ResponseEntity<RestResponse<Void>> updatePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        return success(userService.updatePassword(oldPassword, newPassword));
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/me/avatar")
    public ResponseEntity<RestResponse<String>> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        return success(userService.updateAvatar(file));
    }

    /**
     * 提交管理员权限申请
     */
    @PostMapping("/apply-for-admin")
    public ResponseEntity<RestResponse<Void>> applyForAdmin(@RequestParam("reason") String reason) {
        return success(userService.applyForAdminRole(reason));
    }

    /**
     * 获取当前用户的管理员申请状态
     */
    @GetMapping("/admin-application/status")
    public ResponseEntity<RestResponse<AdminApplicationStatus>> getAdminApplicationStatus() {
        return success(userService.getAdminApplicationStatus());
    }
}