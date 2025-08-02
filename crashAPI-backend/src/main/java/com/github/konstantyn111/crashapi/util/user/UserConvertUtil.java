package com.github.konstantyn111.crashapi.util.user;

import com.github.konstantyn111.crashapi.dto.user.AdminApplicationDTO;
import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.entity.user.AdminApplication;
import com.github.konstantyn111.crashapi.entity.user.Role;
import com.github.konstantyn111.crashapi.entity.user.User;

import java.util.stream.Collectors;

public class UserConvertUtil {

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

        // 转换角色
        if (user.getRoles() != null) {
            userInfo.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }

        return userInfo;
    }

    public static AdminApplicationDTO convertToAdminAppDTO(AdminApplication application) {
        if (application == null) return null;

        AdminApplicationDTO dto = new AdminApplicationDTO();
        dto.setId(application.getId());
        dto.setUserId(application.getUserId());
        dto.setStatus(application.getStatus());
        dto.setCreatedAt(application.getCreatedAt());
        dto.setProcessedAt(application.getProcessedAt());

        // 设置申请人信息
        if (application.getUser() != null) {
            dto.setUsername(application.getUser().getUsername());
            dto.setEmail(application.getUser().getEmail());
        }

        // 设置处理人信息
        if (application.getProcessor() != null) {
            dto.setProcessorUsername(application.getProcessor().getUsername());
        }

        return dto;
    }
}