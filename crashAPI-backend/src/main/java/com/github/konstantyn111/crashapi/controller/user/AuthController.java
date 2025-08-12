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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册，验证唯一性后生成双令牌
     */
    @PostMapping("/register")
    public RestResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * 用户登录，验证身份后生成双令牌
     */
    @PostMapping("/login")
    public RestResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * 用户登出，清除刷新令牌和上下文
     */
    @PostMapping("/logout")
    public RestResponse<Void> logout() {
        return authService.logout();
    }

    /**
     * 令牌刷新，使用旧刷新令牌换取新双令牌
     */
    @PostMapping("/refresh-token")
    public RestResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        return authService.refreshToken(request);
    }
}