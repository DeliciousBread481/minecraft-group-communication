package com.github.konstantyn111.crashapi.controller.user;

import com.github.konstantyn111.crashapi.dto.user.AuthResponse;
import com.github.konstantyn111.crashapi.dto.user.LoginRequest;
import com.github.konstantyn111.crashapi.dto.user.RefreshRequest;
import com.github.konstantyn111.crashapi.dto.user.RegisterRequest;
import com.github.konstantyn111.crashapi.service.user.AuthService;
import com.github.konstantyn111.crashapi.util.RestResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证管理控制器
 * <p>
 * 提供用户注册、登录、登出和令牌刷新等核心认证功能。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册认证
     * <p>
     * 注册新用户并完成认证流程。验证用户名和邮箱唯一性后，加密存储密码，
     * 分配默认用户角色，生成访问令牌和刷新令牌，并将刷新令牌存入数据库。
     * </p>
     *
     * @param request 注册请求体（包含用户名、邮箱和密码）
     * @return 包含双令牌的认证响应实体
     */
    @PostMapping("/register")
    public RestResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * 用户登录认证
     * <p>
     * 认证用户身份并颁发令牌。验证用户名和密码后生成新的双令牌，
     * 更新刷新令牌存储，并设置安全上下文。
     * </p>
     *
     * @param request 登录请求体（包含用户名和密码）
     * @return 包含双令牌的认证响应实体
     */
    @PostMapping("/login")
    public RestResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * 用户登出操作
     * <p>
     * 清除当前认证用户的刷新令牌和安全上下文。
     * 需要有效的用户会话，未登录状态下操作会抛出异常。
     * </p>
     *
     * @return 操作结果响应（无数据体）
     */
    @PostMapping("/logout")
    public RestResponse<Void> logout() {
        return authService.logout();
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
     */
    @PostMapping("/refresh-token")
    public RestResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        return authService.refreshToken(request);
    }
}