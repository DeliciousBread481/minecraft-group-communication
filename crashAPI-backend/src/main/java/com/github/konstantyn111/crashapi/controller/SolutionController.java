package com.github.konstantyn111.crashapi.controller;

import com.github.konstantyn111.crashapi.dto.solution.*;
import com.github.konstantyn111.crashapi.service.solution.SolutionService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 解决方案管理控制器
 * <p>
 * 提供解决方案的创建、查询、更新和状态管理功能，包含公共接口、管理员接口和开发者接口。
 * </p>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    // ==================== 公共接口 - 无权限要求 ====================

    /**
     * 分页获取已发布的解决方案
     * <p>
     * 查询状态为PUBLISHED的解决方案，可按分类ID过滤。
     * </p>
     *
     * @param pageable 分页参数
     * @return 分页的解决方案列表响应实体
     */
    @GetMapping("/solutions")
    public RestResponse<SolutionPageDto> getPublishedSolutions(Pageable pageable) {
        SolutionPageDto pageDto = solutionService.getPublishedSolutions(pageable);
        return RestResponse.success(pageDto, "获取解决方案列表成功");
    }

    /**
     * 根据ID获取解决方案详情（公开）
     * <p>
     * 仅返回状态为PUBLISHED的解决方案详情。
     * </p>
     *
     * @param solutionId 解决方案ID
     * @return 解决方案详情响应实体
     */
    @GetMapping("/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> getSolutionById(
            @PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.getSolutionById(solutionId));
    }

    /**
     * 获取所有问题分类
     * <p>
     * 返回所有可用的问题分类列表。
     * </p>
     *
     * @return 分类列表响应实体
     */
    @GetMapping("/solutions/categories")
    public ResponseEntity<RestResponse<List<CategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(solutionService.getAllCategories());
    }

    // ==================== 管理员接口 - ROLE_ADMIN ====================

    /**
     * 创建解决方案
     * <p>
     * 创建新的解决方案草稿（状态为DRAFT）。
     * </p>
     *
     * @param createDTO 解决方案创建数据传输对象
     * @return 创建的解决方案详情响应实体
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/solutions")
    public ResponseEntity<RestResponse<SolutionDTO>> createSolution(@RequestBody SolutionCreateDTO createDTO) {
        return ResponseEntity.ok(solutionService.createSolution(createDTO));
    }

    /**
     * 更新解决方案（管理员）
     * <p>
     * 更新解决方案基本信息、步骤和图片。
     * </p>
     *
     * @param solutionId 要更新的解决方案ID
     * @param updateDTO 解决方案更新数据传输对象
     * @return 更新后的解决方案详情响应实体
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> updateSolutionAdmin(
            @PathVariable String solutionId,
            @RequestBody SolutionUpdateDTO updateDTO) {
        return ResponseEntity.ok(solutionService.updateSolutionAdmin(solutionId, updateDTO));
    }

    /**
     * 删除解决方案
     * <p>
     * 永久删除解决方案及其关联数据。
     * </p>
     *
     * @param solutionId 要删除的解决方案ID
     * @return 操作结果响应（无数据体）
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/solutions/{solutionId}")
    public ResponseEntity<RestResponse<Void>> deleteSolution(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.deleteSolution(solutionId));
    }

    /**
     * 撤回已发布解决方案
     * <p>
     * 将解决方案状态从PUBLISHED改为DRAFT。
     * </p>
     *
     * @param solutionId 要撤回的解决方案ID
     * @return 操作结果响应（无数据体）
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/solutions/{solutionId}/withdraw")
    public ResponseEntity<RestResponse<Void>> withdrawSolution(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.withdrawSolution(solutionId));
    }

    /**
     * 提交解决方案审核
     * <p>
     * 将解决方案状态从DRAFT改为PENDING_REVIEW。
     * </p>
     *
     * @param solutionId 要提交审核的解决方案ID
     * @return 操作结果响应（无数据体）
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/solutions/{solutionId}/submit-review")
    public ResponseEntity<RestResponse<Void>> submitSolutionForReview(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.submitSolutionForReview(solutionId));
    }

    /**
     * 分页获取管理员创建的解决方案
     * <p>
     * 查询当前管理员创建的所有解决方案（可按状态过滤）。
     * </p>
     *
     * @param pageable 分页参数
     * @param status 解决方案状态（可选过滤条件）
     * @return 分页的解决方案列表响应实体
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/solutions/my")
    public ResponseEntity<RestResponse<Page<SolutionDTO>>> getMySolutions(
            Pageable pageable,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(solutionService.getMySolutions(pageable, status));
    }

    /**
     * 根据ID获取解决方案详情（管理员视图）
     * <p>
     * 返回任意状态的解决方案详情（仅限管理员创建的解决方案）。
     * </p>
     *
     * @param solutionId 解决方案ID
     * @return 解决方案详情响应实体
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> getSolutionByIdAdmin(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.getSolutionByIdAdmin(solutionId));
    }

    // ==================== 开发者接口 - ROLE_DEV ====================

    /**
     * 分页获取待审核的解决方案
     * <p>
     * 查询状态为PENDING_REVIEW的解决方案列表。
     * </p>
     *
     * @param pageable 分页参数
     * @return 分页的解决方案列表响应实体
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @GetMapping("/developer/solutions/pending")
    public ResponseEntity<RestResponse<Page<SolutionDTO>>> getPendingSolutions(Pageable pageable) {
        return ResponseEntity.ok(solutionService.getPendingSolutions(pageable));
    }

    /**
     * 批准解决方案发布
     * <p>
     * 将解决方案状态从PENDING_REVIEW改为PUBLISHED。
     * </p>
     *
     * @param solutionId 要批准的解决方案ID
     * @return 操作结果响应（无数据体）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/developer/solutions/{solutionId}/approve")
    public ResponseEntity<RestResponse<Void>> approveSolution(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.approveSolution(solutionId));
    }

    /**
     * 拒绝解决方案发布
     * <p>
     * 将解决方案状态从PENDING_REVIEW改为DRAFT，可附加拒绝原因。
     * </p>
     *
     * @param solutionId 要拒绝的解决方案ID
     * @param reason 拒绝原因（可选）
     * @return 操作结果响应（无数据体）
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/developer/solutions/{solutionId}/reject")
    public ResponseEntity<RestResponse<Void>> rejectSolution(
            @PathVariable String solutionId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(solutionService.rejectSolution(solutionId, reason));
    }

    /**
     * 更新已发布的解决方案（开发者）
     * <p>
     * 更新已发布解决方案的基本信息、步骤和图片。
     * </p>
     *
     * @param solutionId 要更新的解决方案ID
     * @param updateDTO 解决方案更新数据传输对象
     * @return 更新后的解决方案详情响应实体
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PutMapping("/developer/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> updateSolutionDeveloper(
            @PathVariable String solutionId,
            @RequestBody SolutionUpdateDTO updateDTO) {
        return ResponseEntity.ok(solutionService.updateSolutionDeveloper(solutionId, updateDTO));
    }
}