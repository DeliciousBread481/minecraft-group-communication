package com.github.konstantyn111.crashapi.service.user;

import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.user.UserConvertUtil;
import com.github.konstantyn111.crashapi.util.user.UserSecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserMapper userMapper;

    /**
     * 根据用户ID获取用户信息（需要管理员权限）
     * <p>
     * 通过用户ID查询用户详细信息，包括角色信息。此操作需要管理员权限。
     * 当用户不存在时返回404错误，系统异常时返回500错误。
     * </p>
     *
     * @param userId 要查询的用户ID
     * @return 包含用户信息的响应实体（成功时附带用户信息）
     * @throws BusinessException 当用户不存在或权限验证失败时抛出
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public RestResponse<UserInfo> getUserInfoById(Long userId) {
        try {
            UserSecurityUtils.validateAdminPermissions(userMapper);
            User user = userMapper.findByIdWithRoles(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND, "用户不存在"));
            return RestResponse.success(UserConvertUtil.convertToUserInfo(user), "获取用户信息成功");
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
     * <p>
     * 将用户的刷新令牌置空，使其当前令牌失效。
     * 需要提供有效的用户名，若用户不存在则返回404错误。
     * </p>
     *
     * @param username 要撤销令牌的用户名
     * @return 操作结果响应（不包含数据体）
     * @throws BusinessException 当用户名对应的用户不存在时抛出
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