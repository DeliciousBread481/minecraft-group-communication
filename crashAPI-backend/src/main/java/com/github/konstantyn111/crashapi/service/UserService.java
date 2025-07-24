package com.github.konstantyn111.crashapi.service;

import com.github.konstantyn111.crashapi.dto.UserInfo;
import com.github.konstantyn111.crashapi.entity.AdminApplication;
import com.github.konstantyn111.crashapi.entity.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.AdminApplicationMapper;
import com.github.konstantyn111.crashapi.mapper.UserMapper;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.ErrorCode;
import com.github.konstantyn111.crashapi.util.UserConvertUtil;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final AdminApplicationMapper adminApplicationMapper;
    private final PasswordEncoder passwordEncoder;

    // 文件存储配置
    private final String uploadDir = "uploads/avatars/";
    private final String cdnBaseUrl = "https://cdn.example.com/avatars/";

    /**
     * 申请管理员权限
     */
    @Transactional
    public RestResponse<Void> applyForAdminRole(String reason) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User currentUser = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            // 检查是否已经是管理员
            if (userMapper.hasRole(currentUser.getId(), "ROLE_ADMIN")) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "您已经是管理员");
            }

            // 检查是否有待处理的申请
            if (adminApplicationMapper.hasPendingApplication(currentUser.getId())) {
                throw new BusinessException(ErrorCode.PENDING_APPLICATION_EXISTS,
                        HttpStatus.CONFLICT,
                        "您已提交过申请，请等待处理");
            }

            // 创建新申请
            AdminApplication application = new AdminApplication();
            application.setUserId(currentUser.getId());
            application.setStatus("PENDING");
            application.setReason(reason);
            application.setCreatedAt(LocalDateTime.now());

            adminApplicationMapper.insert(application);

            return RestResponse.success("管理员申请已提交，请等待审核");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "提交申请失败: " + ex.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @Transactional(readOnly = true)
    public RestResponse<UserInfo> getCurrentUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        "用户未登录");
            }

            String username = authentication.getName();

            Optional<User> userOpt = userMapper.findByUsername(username);
            return userOpt.map(user -> RestResponse.success(UserConvertUtil.convertToUserInfo(user), "获取用户信息成功"))
                    .orElseGet(() -> RestResponse.fail(HttpStatus.NOT_FOUND.value(),
                            ErrorCode.USER_NOT_FOUND,
                            "用户不存在"));

        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取用户信息失败: " + ex.getMessage());
        }
    }

    /**
     * 更新当前用户信息
     */
    @Transactional
    public RestResponse<UserInfo> updateUserInfo(UserInfo updateData) {
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

            return RestResponse.success(UserConvertUtil.convertToUserInfo(updatedUser), "用户信息更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新用户信息失败: " + ex.getMessage());
        }
    }

    /**
     * 修改当前用户密码
     */
    @Transactional
    public RestResponse<Void> updatePassword(String oldPassword, String newPassword) {
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

            return RestResponse.success("密码更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新密码失败: " + ex.getMessage());
        }
    }

    /**
     * 更新用户头像
     */
    @Transactional
    public RestResponse<String> updateAvatar(MultipartFile file) {
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

            return RestResponse.success(fileUrl, "头像更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新头像失败: " + ex.getMessage());
        }
    }

    /**
     * 验证邮箱唯一性
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
}