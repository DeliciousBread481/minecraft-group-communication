package com.github.konstantyn111.crashapi.service;

import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.entity.Role;
import com.github.konstantyn111.crashapi.entity.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.UserMapper;
import com.github.konstantyn111.crashapi.util.ApiResponse;
import com.github.konstantyn111.crashapi.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户信息服务
 * <p>
 * 处理用户信息管理、密码修改和头像更新等操作
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // 文件存储配置
    private final String uploadDir = "uploads/avatars/";
    private final String cdnBaseUrl = "https://cdn.example.com/avatars/";

    /**
     * 获取当前登录用户信息
     * <p>从安全上下文中提取用户信息并转换为DTO格式</p>
     *
     * @return 用户信息响应
     */
    @Transactional(readOnly = true)
    public ApiResponse<UserInfo> getCurrentUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        "用户未登录");
            }

            String username = authentication.getName();

            Optional<User> userOpt = userMapper.findByUsername(username);
            return userOpt.map(user -> ApiResponse.success(convertToUserInfo(user), "获取用户信息成功")).orElseGet(() -> ApiResponse.fail(HttpStatus.NOT_FOUND.value(),
                    ErrorCode.USER_NOT_FOUND,
                    "用户不存在"));

        } catch (BusinessException ex) {
            return ApiResponse.fail(ex);
        } catch (Exception ex) {
            return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取用户信息失败: " + ex.getMessage());
        }
    }

    /**
     * 更新当前用户信息
     * <p>允许更新昵称、邮箱等基本信息</p>
     *
     * @param updateData 更新数据
     * @return 更新后的用户信息
     */
    @Transactional
    public ApiResponse<UserInfo> updateUserInfo(UserInfo updateData) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User existingUser = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            // 更新基本信息
            if (updateData.getNickname() != null) {
                existingUser.setNickname(updateData.getNickname());
            }

            if (updateData.getEmail() != null && !updateData.getEmail().equals(existingUser.getEmail())) {
                validateEmailUniqueness(updateData.getEmail(), existingUser.getId());
                existingUser.setEmail(updateData.getEmail());
            }

            // 更新数据库
            userMapper.updateUserInfo(existingUser);

            // 重新加载用户信息
            User updatedUser = userMapper.findByIdWithRoles(existingUser.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            return ApiResponse.success(convertToUserInfo(updatedUser), "用户信息更新成功");
        } catch (BusinessException ex) {
            return ApiResponse.fail(ex);
        } catch (Exception ex) {
            return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新用户信息失败: " + ex.getMessage());
        }
    }

    /**
     * 修改当前用户密码
     * <p>验证旧密码后更新为新密码</p>
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    @Transactional
    public ApiResponse<Void> updatePassword(String oldPassword, String newPassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User existingUser = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_CREDENTIALS,
                        HttpStatus.UNAUTHORIZED,
                        "旧密码不正确");
            }

            // 更新密码
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            userMapper.updateUserInfo(existingUser);

            return ApiResponse.success("密码更新成功");
        } catch (BusinessException ex) {
            return ApiResponse.fail(ex);
        } catch (Exception ex) {
            return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新密码失败: " + ex.getMessage());
        }
    }

    /**
     * 更新用户头像
     * <p>处理头像文件上传并更新用户头像URL</p>
     *
     * @param file 头像文件
     * @return 包含新头像URL的响应
     */
    @Transactional
    public ApiResponse<String> updateAvatar(MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User existingUser = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            // 处理文件上传
            String fileUrl = storeAvatarFile(file, existingUser.getId());

            // 更新用户头像URL
            existingUser.setAvatar(fileUrl);
            userMapper.updateUserInfo(existingUser);

            return ApiResponse.success(fileUrl, "头像更新成功");
        } catch (BusinessException ex) {
            return ApiResponse.fail(ex);
        } catch (Exception ex) {
            return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新头像失败: " + ex.getMessage());
        }
    }

    /**
     * 验证邮箱唯一性
     * @param email 待验证邮箱
     * @param currentUserId 当前用户ID
     * @throws BusinessException 邮箱已被其他用户使用时抛出
     */
    private void validateEmailUniqueness(String email, Long currentUserId) {
        Optional<User> userByEmail = userMapper.findByEmail(email);
        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL,
                    HttpStatus.CONFLICT,
                    "邮箱已被使用");
        }
    }

    /**
     * 存储头像文件
     * <p>保存上传文件到本地目录并返回访问URL</p>
     *
     * @param file 上传文件
     * @param userId 用户ID
     * @return 文件访问URL
     * @throws IOException 文件操作失败时抛出
     */
    private String storeAvatarFile(MultipartFile file, Long userId) throws IOException {
        // 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String fileName = "avatar_" + userId + "_" + UUID.randomUUID() + extension;

        // 保存文件
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 返回访问URL
        return cdnBaseUrl + fileName;
    }

    /**
     * 用户实体转DTO
     * <p>提取用户核心信息并转换角色集合</p>
     *
     * @param user 用户实体
     * @return 用户信息DTO
     */
    private UserInfo convertToUserInfo(User user) {
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
}