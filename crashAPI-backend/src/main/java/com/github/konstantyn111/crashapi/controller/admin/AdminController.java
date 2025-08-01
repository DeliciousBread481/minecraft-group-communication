package com.github.konstantyn111.crashapi.controller.admin;

import com.github.konstantyn111.crashapi.dto.*;
import com.github.konstantyn111.crashapi.dto.solution.SolutionCreateDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.service.admin.AdminService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 创建解决方案
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/solutions")
    public ResponseEntity<RestResponse<SolutionDTO>> createSolution(@RequestBody SolutionCreateDTO createDTO) {
        return ResponseEntity.ok(adminService.createSolution(createDTO));
    }

    /**
     * 更新解决方案
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> updateSolution(
            @PathVariable String solutionId,
            @RequestBody SolutionUpdateDTO updateDTO) {
        return ResponseEntity.ok(adminService.updateSolution(solutionId, updateDTO));
    }

    /**
     * 删除解决方案
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/solutions/{solutionId}")
    public ResponseEntity<RestResponse<Void>> deleteSolution(@PathVariable String solutionId) {
        return ResponseEntity.ok(adminService.deleteSolution(solutionId));
    }

    /**
     * 撤回已发布解决方案
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/solutions/{solutionId}/withdraw")
    public ResponseEntity<RestResponse<Void>> withdrawSolution(@PathVariable String solutionId) {
        return ResponseEntity.ok(adminService.withdrawSolution(solutionId));
    }

    /**
     * 提交解决方案审核
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/solutions/{solutionId}/submit-review")
    public ResponseEntity<RestResponse<Void>> submitSolutionForReview(@PathVariable String solutionId) {
        return ResponseEntity.ok(adminService.submitSolutionForReview(solutionId));
    }

    /**
     * 分页获取管理员创建的解决方案
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/solutions/my")
    public ResponseEntity<RestResponse<Page<SolutionDTO>>> getMySolutions(
            Pageable pageable,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getMySolutions(pageable, status));
    }

    /**
     * 根据ID获取解决方案详情
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> getSolutionById(@PathVariable String solutionId) {
        return ResponseEntity.ok(adminService.getSolutionById(solutionId));
    }
}