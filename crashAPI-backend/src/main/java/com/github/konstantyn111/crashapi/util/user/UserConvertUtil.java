package com.github.konstantyn111.crashapi.util.user;

import com.github.konstantyn111.crashapi.dto.user.AdminApplicationDTO;
import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.entity.user.AdminApplication;
import com.github.konstantyn111.crashapi.entity.user.Role;
import com.github.konstantyn111.crashapi.entity.user.User;

import java.util.stream.Collectors;

/**
 * 用户数据转换工具类
 * <p>
 * 提供用户实体与数据传输对象之间的转换功能。
 * </p>
 */
public class UserConvertUtil {

    /**
     * 将用户实体转换为用户信息DTO
     * <p>
     * 提取用户基本信息并转换角色集合为角色名称集合。
     * </p>
     *
     * @param user 要转换的用户实体
     * @return 用户信息DTO（当输入为null时返回null）
     */
    public static UserInfo convertToUserInfo(User user) {
        if (user == null) return null;

        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setCreatedAt(user.getCreatedAt());
        userInfo.setUpdatedAt(user.getUpdatedAt());
        userInfo.setEnabled(user.isEnabled());

        if (user.getRoles() != null) {
            userInfo.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }

        return userInfo;
    }

    /**
     * 将管理员申请实体转换为DTO
     * <p>
     * 提取申请基本信息并关联申请人/处理人信息。
     * </p>
     *
     * @param application 要转换的管理员申请实体
     * @return 管理员申请DTO（当输入为null时返回null）
     */
    public static AdminApplicationDTO convertToAdminAppDTO(AdminApplication application) {
        if (application == null) return null;

        AdminApplicationDTO dto = new AdminApplicationDTO();
        dto.setId(application.getId());
        dto.setUserId(application.getUserId());
        dto.setStatus(application.getStatus());
        dto.setCreatedAt(application.getCreatedAt());
        dto.setProcessedAt(application.getProcessedAt());

        if (application.getUser() != null) {
            dto.setUsername(application.getUser().getUsername());
            dto.setEmail(application.getUser().getEmail());
        }

        if (application.getProcessor() != null) {
            dto.setProcessorUsername(application.getProcessor().getUsername());
        }

        return dto;
    }
}