package com.github.konstantyn111.crashapi.controller.user;

import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.service.user.AdminService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 根据ID获取用户信息
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<RestResponse<UserInfo>> getUserInfoById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserInfoById(userId));
    }

    /**
     * 撤销用户令牌
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/users/revoke-token")
    public ResponseEntity<RestResponse<Void>> revokeToken(@RequestParam String username) {
        return ResponseEntity.ok(adminService.revokeToken(username));
    }

}