package com.github.konstantyn111.crashapi.service;

import com.github.konstantyn111.crashapi.dto.AuthResponse;
import com.github.konstantyn111.crashapi.dto.LoginRequest;
import com.github.konstantyn111.crashapi.dto.RefreshRequest;
import com.github.konstantyn111.crashapi.dto.RegisterRequest;
import com.github.konstantyn111.crashapi.entity.User;
import com.github.konstantyn111.crashapi.mapper.RoleMapper;
import com.github.konstantyn111.crashapi.mapper.UserMapper;
import com.github.konstantyn111.crashapi.security.CustomUserDetails;
import com.github.konstantyn111.crashapi.util.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        // 检查用户名唯一性
        if (userMapper.findByUsername(request.getUsername()).isPresent()) {
            return ApiResponse.fail(HttpStatus.CONFLICT.value(),
                    "用户名已被使用",
                    null);
        }

        // 检查邮箱唯一性
        if (userMapper.findByEmail(request.getEmail()).isPresent()) {
            return ApiResponse.fail(HttpStatus.CONFLICT.value(),
                    "邮箱已被注册",
                    null);
        }

        // 创建并保存用户
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        userMapper.save(user);

        // 分配默认角色
        roleMapper.findByName("ROLE_USER")
                .ifPresent(role -> userMapper.addRoleToUser(user.getId(), role.getId()));

        // 生成双令牌
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // 存储刷新令牌到数据库
        userMapper.updateRefreshToken(user.getId(), refreshToken,
                new Date(System.currentTimeMillis() + jwtService.getJwtRefreshExpiration()));

        // 构建响应
        AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken,
                "注册成功"
        );

        return ApiResponse.success(authResponse, "用户注册成功");
    }

    public ApiResponse<AuthResponse> login(LoginRequest request) {
        try {
            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户详情
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 生成双令牌
            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            // 存储刷新令牌到数据库
            userMapper.updateRefreshToken(user.getId(), refreshToken,
                    new Date(System.currentTimeMillis() + jwtService.getJwtRefreshExpiration()));

            // 构建响应
            AuthResponse authResponse = new AuthResponse(
                    accessToken,
                    refreshToken,
                    "登录成功"
            );

            return ApiResponse.success(authResponse, "用户登录成功");
        } catch (Exception e) {
            logger.error("登录失败: {}", e.getMessage());
            return ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(),
                    "用户名或密码错误",
                    null);
        }
    }

    @Transactional
    public ApiResponse<AuthResponse> refreshToken(RefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // 1. 基本验证
            if (!jwtService.isRefreshTokenValid(refreshToken)) {
                return ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(),
                        "刷新令牌无效",
                        null);
            }

            // 2. 提取用户名
            String username = jwtService.extractUsername(refreshToken);
            if (username == null) {
                return ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(),
                        "无法从令牌中提取用户信息",
                        null);
            }

            // 3. 获取用户信息
            Optional<User> userOptional = userMapper.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(),
                        "用户不存在",
                        null);
            }

            // 4. 创建UserDetails
            CustomUserDetails userDetails = new CustomUserDetails(userOptional.get());

            // 5. 验证令牌匹配
            if (!refreshToken.equals(userDetails.getRefreshToken())) {
                return ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(),
                        "刷新令牌不匹配",
                        null);
            }

            // 6. 检查令牌状态
            if (!userDetails.isRefreshTokenValid()) {
                return ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(),
                        "刷新令牌已过期",
                        null);
            }

            // 7. 生成新令牌对
            String newAccessToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            // 8. 更新数据库中的刷新令牌
            userMapper.updateRefreshToken(
                    userDetails.getUser().getId(),
                    newRefreshToken,
                    new Date(System.currentTimeMillis() + jwtService.getJwtRefreshExpiration())
            );

            // 9. 构建响应
            AuthResponse authResponse = new AuthResponse(
                    newAccessToken,
                    newRefreshToken,
                    "令牌刷新成功"
            );

            return ApiResponse.success(authResponse, "令牌刷新成功");

        } catch (ExpiredJwtException ex) {
            return ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(),
                    "刷新令牌已过期，请重新登录",
                    null);
        } catch (Exception ex) {
            logger.error("令牌刷新失败: {}", ex.getMessage());
            return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "令牌刷新失败",
                    null);
        }
    }

    @Transactional
    public ApiResponse<Void> revokeToken(String username) {
        Optional<User> userOptional = userMapper.findByUsername(username);
        if (userOptional.isPresent()) {
            userMapper.updateRefreshToken(
                    userOptional.get().getId(),
                    null,
                    null
            );
            return ApiResponse.success(null, "令牌已撤销");
        }
        return ApiResponse.fail(HttpStatus.NOT_FOUND.value(),
                "用户不存在",
                null);
    }
}