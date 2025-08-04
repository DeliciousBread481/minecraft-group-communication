package com.github.konstantyn111.crashapi.service.user;

import com.github.konstantyn111.crashapi.dto.user.AuthResponse;
import com.github.konstantyn111.crashapi.dto.user.LoginRequest;
import com.github.konstantyn111.crashapi.dto.user.RefreshRequest;
import com.github.konstantyn111.crashapi.dto.user.RegisterRequest;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.user.RoleMapper;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import com.github.konstantyn111.crashapi.security.CustomUserDetails;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

    /**
     * 用户注册认证
     * <p>
     * 注册新用户并完成认证流程。验证用户名和邮箱唯一性后，加密存储密码，
     * 分配默认用户角色，生成访问令牌和刷新令牌，并将刷新令牌存入数据库。
     * </p>
     *
     * @param request 注册请求体（包含用户名、邮箱和密码）
     * @return 包含双令牌的认证响应实体
     * @throws BusinessException 当用户名或邮箱已被注册时抛出
     */
    @Transactional
    public RestResponse<AuthResponse> register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME,
                    HttpStatus.CONFLICT,
                    "用户名已被使用");
        }

        if (userMapper.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL,
                    HttpStatus.CONFLICT,
                    "邮箱已被注册");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        userMapper.save(user);

        roleMapper.findByName("ROLE_USER")
                .ifPresent(role -> userMapper.addRoleToUser(user.getId(), role.getId()));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        userMapper.updateRefreshToken(user.getId(), refreshToken,
                new Date(System.currentTimeMillis() + jwtService.getJwtRefreshExpiration()));

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken,
                "注册成功"
        );

        return RestResponse.success(authResponse, "用户注册成功");
    }

    /**
     * 用户登录认证
     * <p>
     * 认证用户身份并颁发令牌。使用Spring Security认证用户名和密码，
     * 认证成功后生成新的双令牌，更新刷新令牌存储，并设置安全上下文。
     * </p>
     *
     * @param request 登录请求体（包含用户名和密码）
     * @return 包含双令牌的认证响应实体
     * @throws BusinessException 当用户名/密码错误或认证失败时抛出
     */
    public RestResponse<AuthResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            userMapper.updateRefreshToken(user.getId(), refreshToken,
                    new Date(System.currentTimeMillis() + jwtService.getJwtRefreshExpiration()));

            AuthResponse authResponse = new AuthResponse(
                    accessToken,
                    refreshToken,
                    "登录成功"
            );

            return RestResponse.success(authResponse, "用户登录成功");
        } catch (BadCredentialsException e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS,
                    HttpStatus.UNAUTHORIZED,
                    "用户名或密码错误");
        } catch (Exception e) {
            logger.error("登录失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "登录过程中发生错误");
        }
    }

    /**
     * 令牌刷新机制
     * <p>
     * 使用有效刷新令牌换取新的双令牌。验证刷新令牌有效性后生成新访问令牌和刷新令牌，
     * 同时更新数据库中的刷新令牌和有效期。
     * </p>
     *
     * @param request 令牌刷新请求体（包含原刷新令牌）
     * @return 包含新双令牌的认证响应实体
     * @throws BusinessException 当令牌无效/过期、用户不存在或刷新令牌不匹配时抛出
     */
    @Transactional
    public RestResponse<AuthResponse> refreshToken(RefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            if (!jwtService.isRefreshTokenValid(refreshToken)) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN,
                        HttpStatus.UNAUTHORIZED,
                        "刷新令牌无效");
            }

            String username = jwtService.extractUsername(refreshToken);
            if (username == null) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN,
                        HttpStatus.UNAUTHORIZED,
                        "无法从令牌中提取用户信息");
            }

            Optional<User> userOptional = userMapper.findByUsername(username);
            if (userOptional.isEmpty()) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.UNAUTHORIZED,
                        "用户不存在");
            }

            CustomUserDetails userDetails = getCustomUserDetails(userOptional, refreshToken);

            String newAccessToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            userMapper.updateRefreshToken(
                    userDetails.getUser().getId(),
                    newRefreshToken,
                    new Date(System.currentTimeMillis() + jwtService.getJwtRefreshExpiration())
            );

            AuthResponse authResponse = new AuthResponse(
                    newAccessToken,
                    newRefreshToken,
                    "令牌刷新成功"
            );

            return RestResponse.success(authResponse, "令牌刷新成功");

        } catch (ExpiredJwtException ex) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED,
                    HttpStatus.UNAUTHORIZED,
                    "刷新令牌已过期，请重新登录");
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("令牌刷新失败: {}", ex.getMessage());
            throw new BusinessException(ErrorCode.UNKNOWN_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "令牌刷新失败");
        }
    }

    /**
     * 获取用户详情并验证令牌
     *
     * @param userOptional 数据库中的用户数据
     * @param refreshToken 待验证的刷新令牌
     * @return 包含用户的认证详情
     * @throws BusinessException 当令牌不匹配或已过期时抛出
     */
    private static @NotNull CustomUserDetails getCustomUserDetails(Optional<User> userOptional, String refreshToken) {
        User user = userOptional.orElseThrow(() ->
                new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.UNAUTHORIZED,
                        "用户不存在"));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!refreshToken.equals(userDetails.getRefreshToken())) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN,
                    HttpStatus.UNAUTHORIZED,
                    "刷新令牌不匹配");
        }

        if (!userDetails.isRefreshTokenValid()) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED,
                    HttpStatus.UNAUTHORIZED,
                    "刷新令牌已过期");
        }
        return userDetails;
    }

    /**
     * 用户登出操作
     * <p>
     * 清除当前认证用户的刷新令牌和安全上下文。
     * 需要有效的用户会话，未登录状态下操作会抛出异常。
     * </p>
     *
     * @return 操作结果响应（无数据体）
     * @throws BusinessException 当用户未登录或用户不存在时抛出
     */
    @Transactional
    public RestResponse<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED,HttpStatus.UNAUTHORIZED,"用户未登录");
        }
        String username = authentication.getName();

        Optional<User> userOptional = userMapper.findByUsername(username);
        if (userOptional.isPresent()) {
            userMapper.updateRefreshToken(
                    userOptional.get().getId(),
                    null,
                    null
            );
        } else {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND,HttpStatus.NOT_FOUND, "用户不存在");
        }

        SecurityContextHolder.clearContext();
        return RestResponse.success(null,"退出登录成功");
    }
}