package com.github.konstantyn111.crashapi.service.user;

import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.user.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserMapper userMapper;

    /**
     * 根据用户ID获取用户信息（需要管理员权限）
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public RestResponse<UserInfo> getUserInfoById(Long userId) {
        try {
            UserUtils.Security.validateAdminPermissions(userMapper);

            User user = userMapper.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND, "用户不存在"));

            Set<String> roles = userMapper.findRolesByUserId(user.getId());

            return RestResponse.success(UserUtils.Convert.toUserInfo(user, roles), "获取用户信息成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取用户信息失败: " + ex.getMessage());
        }
    }

    /**
     * 撤销指定用户的令牌
     */
    @Transactional
    public RestResponse<Void> revokeToken(String username) {
        Optional<User> userOptional = userMapper.findByUsername(username);
        if (userOptional.isPresent()) {
            userMapper.updateRefreshToken(
                    userOptional.get().getId(),
                    null,
                    null
            );
            return RestResponse.success(null, "令牌已撤销");
        }
        throw new BusinessException(ErrorCode.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND,
                "用户不存在");
    }
}