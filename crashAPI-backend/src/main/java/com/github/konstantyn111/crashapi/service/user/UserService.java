package com.github.konstantyn111.crashapi.service.user;

import com.github.konstantyn111.crashapi.dto.user.AdminApplicationStatus;
import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.entity.user.AdminApplication;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.user.AdminApplicationMapper;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import com.github.konstantyn111.crashapi.service.solution.FileStorageService;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.SecurityValidationUtils;
import com.github.konstantyn111.crashapi.util.user.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final AdminApplicationMapper adminApplicationMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final SecurityValidationUtils securityValidatorUtils;

    /**
     * 提交管理员权限申请
     * @param reason 申请理由说明
     * @throws BusinessException 当用户已是管理员或有待处理申请时抛出
     */
    @Transactional
    public RestResponse<Void> applyForAdminRole(String reason) {
        try {
            // 安全检查
            String safeReason = securityValidatorUtils.fullSecurityCheck(reason);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User currentUser = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            if (userMapper.hasRole(currentUser.getId(), "ROLE_ADMIN")) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "您已经是管理员");
            }

            if (adminApplicationMapper.hasPendingApplication(currentUser.getId())) {
                throw new BusinessException(ErrorCode.PENDING_APPLICATION_EXISTS,
                        HttpStatus.CONFLICT,
                        "您已提交过申请，请等待处理");
            }

            AdminApplication application = new AdminApplication();
            application.setApplicantId(currentUser.getId());
            application.setStatus("PENDING");
            application.setReason(safeReason);
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

    @Transactional(readOnly = true)
    public RestResponse<AdminApplicationStatus> getAdminApplicationStatus() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        "用户未登录");
            }

            String username = authentication.getName();
            User currentUser = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));
            Optional<AdminApplication> applicationOpt =
                    adminApplicationMapper.findLatestByUserId(currentUser.getId());

            if (userMapper.hasRole(currentUser.getId(), "ROLE_ADMIN")) {
                throw new BusinessException(ErrorCode.ALREADY_ADMIN,
                        HttpStatus.BAD_REQUEST,
                        "您已经是管理员");
            }
            if (applicationOpt.isEmpty()) {
                return RestResponse.fail(HttpStatus.NOT_FOUND.value(),
                        ErrorCode.APPLICATION_NOT_FOUND,
                        "未找到管理员申请记录");
            }

            AdminApplication application = applicationOpt.get();

            AdminApplicationStatus dto = AdminApplicationStatus.builder()
                    .id(application.getId())
                    .applicantId(application.getApplicantId())
                    .status(application.getStatus())
                    .reason(application.getReason())
                    .feedback(application.getFeedback())
                    .createdAt(application.getCreatedAt())
                    .build();

            return RestResponse.success(dto, "获取管理员申请状态成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取申请状态失败: " + ex.getMessage());
        }
    }


    /**
     * 获取当前登录用户信息
     * @throws BusinessException 当用户未登录或用户不存在时抛出
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

            if (userOpt.isEmpty()) {
                return RestResponse.fail(HttpStatus.NOT_FOUND.value(),
                        ErrorCode.USER_NOT_FOUND,
                        "用户不存在");
            }

            User user = userOpt.get();
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
     * 更新用户基本信息
     * @param updateData 包含更新字段的用户信息对象
     * @throws BusinessException 当邮箱已被使用或用户不存在时抛出
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

            if (updateData.getNickname() != null) {
                existingUser.setNickname(updateData.getNickname());
            }

            if (updateData.getEmail() != null && !updateData.getEmail().equals(existingUser.getEmail())) {
                validateEmailUniqueness(updateData.getEmail(), existingUser.getId());
                existingUser.setEmail(updateData.getEmail());
            }

            existingUser.setUpdatedAt(LocalDateTime.now());
            userMapper.updateUserInfo(existingUser);

            User updatedUser = userMapper.findById(existingUser.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));
            Set<String> roles = userMapper.findRolesByUserId(updatedUser.getId());

            return RestResponse.success(UserUtils.Convert.toUserInfo(updatedUser, roles), "用户信息更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新用户信息失败: " + ex.getMessage());
        }
    }

    /**
     * 修改用户密码
     * @param oldPassword 原密码（明文）
     * @param newPassword 新密码（明文）
     * @throws BusinessException 当旧密码错误或用户不存在时抛出
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

            if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_CREDENTIALS,
                        HttpStatus.UNAUTHORIZED,
                        "旧密码不正确");
            }

            existingUser.setPassword(passwordEncoder.encode(newPassword));
            existingUser.setUpdatedAt(LocalDateTime.now());
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
     * @param file 头像图片文件
     * @throws BusinessException 当文件格式/大小不符或用户不存在时抛出
     */
    @Transactional
    public RestResponse<String> updateAvatar(MultipartFile file) {
        User existingUser = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            existingUser = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            String fileName = "avatar_" + existingUser.getId() + "_" + System.currentTimeMillis();
            String storedFileName = fileStorageService.storeFile(
                    file,
                    "avatars",
                    fileName,
                    2 * 1024 * 1024,
                    "image/"
            );

            String avatarCdnBaseUrl = "https://cdn.example.com/avatars/";
            String fileUrl = avatarCdnBaseUrl + storedFileName;
            existingUser.setAvatar(fileUrl);
            existingUser.setUpdatedAt(LocalDateTime.now());
            userMapper.updateUserInfo(existingUser);

            return RestResponse.success(fileUrl, "头像更新成功");
        } catch (BusinessException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (existingUser != null) {
                log.error("更新失败！用户ID: {}", existingUser.getId(), ex);
            }
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新头像失败: " + ex.getMessage());
        }
    }

    /**
     * 验证邮箱地址唯一性
     * @param email 待验证的邮箱地址
     * @param currentUserId 当前用户ID
     * @throws BusinessException 当邮箱已被其他用户使用时抛出
     */
    private void validateEmailUniqueness(String email, Long currentUserId) {
        Optional<User> userByEmail = userMapper.findByEmail(email);
        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL,
                    HttpStatus.CONFLICT,
                    "邮箱已被使用");
        }
    }
}