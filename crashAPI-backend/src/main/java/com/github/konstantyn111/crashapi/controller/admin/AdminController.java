package com.github.konstantyn111.crashapi.controller.admin;

import com.github.konstantyn111.crashapi.dto.*;
import com.github.konstantyn111.crashapi.dto.solution.CategoryDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionCreateDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.service.admin.AdminService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ===================== 用户管理接口 =====================

    /**
     * 根据ID获取用户信息
     * @param userId 目标用户ID
     * @return 用户信息响应
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RestResponse<UserInfo> getUserById(@PathVariable Long userId) {
        return adminService.getUserInfoById(userId);
    }

    /**
     * 撤销指定用户的所有令牌
     * @param username 需要撤销令牌的用户名
     * @return 操作结果（无数据返回）
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/revoke-token")
    public RestResponse<Void> revokeToken(@RequestParam String username) {
        return adminService.revokeToken(username);
    }

    // ===================== 解决方案管理接口 =====================

    /**
     * 创建解决方案
     * @param createDTO 解决方案创建DTO
     * @return 创建的解决方案DTO
     */
    @PostMapping("/solutions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RestResponse<SolutionDTO> createSolution(@RequestBody SolutionCreateDTO createDTO) {
        return adminService.createSolution(createDTO);
    }

    /**
     * 更新解决方案
     * @param solutionId 解决方案ID
     * @param updateDTO 解决方案更新DTO
     * @return 更新后的解决方案DTO
     */
    @PutMapping("/solutions/{solutionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RestResponse<SolutionDTO> updateSolution(
            @PathVariable String solutionId,
            @RequestBody SolutionUpdateDTO updateDTO) {
        return adminService.updateSolution(solutionId, updateDTO);
    }

    /**
     * 删除解决方案
     * @param solutionId 解决方案ID
     * @return 删除结果
     */
    @DeleteMapping("/solutions/{solutionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RestResponse<Void> deleteSolution(@PathVariable String solutionId) {
        return adminService.deleteSolution(solutionId);
    }

    /**
     * 撤回已发布的解决方案
     * @param solutionId 解决方案ID
     * @return 操作结果
     */
    @PostMapping("/solutions/{solutionId}/withdraw")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RestResponse<Void> withdrawSolution(@PathVariable String solutionId) {
        return adminService.withdrawSolution(solutionId);
    }

    /**
     * 提交解决方案审核
     * @param solutionId 解决方案ID
     * @return 提交结果
     */
    @PostMapping("/solutions/{solutionId}/submit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RestResponse<Void> submitSolutionForReview(@PathVariable String solutionId) {
        return adminService.submitSolutionForReview(solutionId);
    }

    /**
     * 获取管理员创建的解决方案列表
     * @param pageable 分页参数
     * @param status 解决方案状态（可选）
     * @return 解决方案分页列表
     */
    @GetMapping("/solutions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RestResponse<Page<SolutionDTO>> getMySolutions(
            Pageable pageable,
            @RequestParam(required = false) String status) {
        return adminService.getMySolutions(pageable, status);
    }

    /**
     * 获取解决方案详情
     * @param solutionId 解决方案ID
     * @return 解决方案详情
     */
    @GetMapping("/solutions/{solutionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RestResponse<SolutionDTO> getSolutionById(@PathVariable String solutionId) {
        return adminService.getSolutionById(solutionId);
    }

    // ===================== 分类管理接口 =====================

    /**
     * 获取所有问题分类
     * @return 分类列表
     */
    @GetMapping("/categories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RestResponse<List<CategoryDTO>> getAllCategories() {
        return adminService.getAllCategories();
    }
}