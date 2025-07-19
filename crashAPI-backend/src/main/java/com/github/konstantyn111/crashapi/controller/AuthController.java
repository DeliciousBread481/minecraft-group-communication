package com.github.konstantyn111.crashapi.controller;

import com.github.konstantyn111.crashapi.dto.AuthResponse;
import com.github.konstantyn111.crashapi.dto.LoginRequest;
import com.github.konstantyn111.crashapi.dto.RefreshRequest;
import com.github.konstantyn111.crashapi.dto.RegisterRequest;
import com.github.konstantyn111.crashapi.service.AuthService;
import com.github.konstantyn111.crashapi.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户注册、登录、令牌刷新等认证相关操作
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     * @param request 注册请求体（包含用户名、密码等信息）
     * @return 包含认证响应（令牌等）的通用响应体
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * 用户登录
     * @param request 登录请求体（包含凭证信息）
     * @return 包含认证响应（访问令牌、刷新令牌）的通用响应体
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * 用户登出
     * @return 操作结果（无数据返回）
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return authService.logout();
    }

    /**
     * 刷新访问令牌
     * @param request 刷新令牌请求体（包含刷新令牌）
     * @return 包含新令牌对的通用响应体
     */
    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        return authService.refreshToken(request);
    }

    /**
     * 撤销指定用户的所有令牌
     * @param username 需要撤销令牌的用户名
     * @return 操作结果（无数据返回）
     */
    @PostMapping("/revoke-token")
    public ApiResponse<Void> revokeToken(@RequestParam String username) {
        return authService.revokeToken(username);
    }
}