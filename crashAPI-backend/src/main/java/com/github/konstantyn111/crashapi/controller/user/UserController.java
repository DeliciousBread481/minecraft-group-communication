package com.github.konstantyn111.crashapi.controller.user;

import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.service.user.UserService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息管理控制器
 * <p>
 * 提供用户信息管理相关接口，包括个人信息获取/更新、密码修改、头像更新和管理员申请。
 * </p>
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户信息
     * <p>
     * 从安全上下文中提取认证信息，返回完整的用户信息（含角色）。
     * </p>
     *
     * @return 包含用户信息的响应实体
     */
    @GetMapping("/me")
    public RestResponse<UserInfo> getCurrentUser() {
        return userService.getCurrentUserInfo();
    }

    /**
     * 更新当前用户基本信息
     * <p>
     * 修改昵称或邮箱信息。邮箱变更时验证唯一性。
     * </p>
     *
     * @param updateData 包含更新字段的用户信息对象
     * @return 更新后的用户信息响应实体
     */
    @PatchMapping("/me")
    public RestResponse<UserInfo> updateUser(@RequestBody UserInfo updateData) {
        return userService.updateUserInfo(updateData);
    }

    /**
     * 修改当前用户密码
     * <p>
     * 验证旧密码匹配后更新为新密码（加密存储）。
     * </p>
     *
     * @param oldPassword 原密码（明文）
     * @param newPassword 新密码（明文）
     * @return 操作结果响应（无数据体）
     */
    @PostMapping("/me/password")
    public RestResponse<Void> updatePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        return userService.updatePassword(oldPassword, newPassword);
    }

    /**
     * 更新用户头像
     * <p>
     * 上传并存储头像文件，验证文件类型和大小后生成唯一文件名。
     * </p>
     *
     * @param file 头像图片文件
     * @return 包含新头像URL的响应实体
     */
    @PostMapping("/me/avatar")
    public RestResponse<String> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        return userService.updateAvatar(file);
    }

    /**
     * 提交管理员权限申请
     * <p>
     * 为当前登录用户创建管理员权限申请。验证用户状态（非管理员且无待处理申请）。
     * </p>
     *
     * @param reason 申请理由说明
     * @return 操作结果响应（无数据体）
     */
    @PostMapping("/apply-for-admin")
    public RestResponse<Void> applyForAdmin(@RequestParam("reason") String reason) {
        return userService.applyForAdminRole(reason);
    }
}