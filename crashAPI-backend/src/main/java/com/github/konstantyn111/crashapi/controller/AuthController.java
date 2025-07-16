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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/revoke-token")
    public ApiResponse<Void> revokeToken(@RequestParam String username) {
        return authService.revokeToken(username);
    }
}