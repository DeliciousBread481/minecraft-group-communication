package com.github.konstantyn111.crashapi.controller.solution;

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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    // ==================== 公共接口 ====================

    /**
     * 分页获取已发布的解决方案
     */
    @GetMapping("/solutions")
    public RestResponse<SolutionPageDto> getPublishedSolutions(Pageable pageable) {
        SolutionPageDto pageDto = solutionService.getPublishedSolutions(pageable);
        return RestResponse.success(pageDto, "获取解决方案列表成功");
    }

    /**
     * 根据ID获取解决方案详情
     */
    @GetMapping("/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> getSolutionById(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.getSolutionById(solutionId));
    }

    /**
     * 获取所有问题分类
     */
    @GetMapping("/solutions/categories")
    public ResponseEntity<RestResponse<List<CategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(solutionService.getAllCategories());
    }

    // ==================== 管理员接口 ====================

    /**
     * 创建解决方案
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/solutions")
    public ResponseEntity<RestResponse<SolutionDTO>> createSolution(@RequestBody SolutionCreateDTO createDTO) {
        return ResponseEntity.ok(solutionService.createSolution(createDTO));
    }

    /**
     * 更新解决方案
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
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/solutions/{solutionId}")
    public ResponseEntity<RestResponse<Void>> deleteSolution(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.deleteSolution(solutionId));
    }

    /**
     * 撤回已发布解决方案
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/solutions/{solutionId}/withdraw")
    public ResponseEntity<RestResponse<Void>> withdrawSolution(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.withdrawSolution(solutionId));
    }

    /**
     * 提交解决方案审核
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/solutions/{solutionId}/submit-review")
    public ResponseEntity<RestResponse<Void>> submitSolutionForReview(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.submitSolutionForReview(solutionId));
    }

    /**
     * 分页获取管理员创建的解决方案
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/solutions/my")
    public ResponseEntity<RestResponse<Page<SolutionDTO>>> getMySolutions(
            Pageable pageable,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(solutionService.getMySolutions(pageable, status));
    }

    /**
     * 根据ID获取解决方案详情
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> getSolutionByIdAdmin(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.getSolutionByIdAdmin(solutionId));
    }

    // ==================== 开发者接口 ====================

    /**
     * 分页获取待审核的解决方案
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @GetMapping("/developer/solutions/pending")
    public ResponseEntity<RestResponse<Page<SolutionDTO>>> getPendingSolutions(Pageable pageable) {
        return ResponseEntity.ok(solutionService.getPendingSolutions(pageable));
    }

    /**
     * 批准解决方案发布
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/developer/solutions/{solutionId}/approve")
    public ResponseEntity<RestResponse<Void>> approveSolution(@PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.approveSolution(solutionId));
    }

    /**
     * 拒绝解决方案发布
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PostMapping("/developer/solutions/{solutionId}/reject")
    public ResponseEntity<RestResponse<Void>> rejectSolution(
            @PathVariable String solutionId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(solutionService.rejectSolution(solutionId, reason));
    }

    /**
     * 更新已发布的解决方案
     */
    @PreAuthorize("hasRole('ROLE_DEV')")
    @PutMapping("/developer/solutions/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> updateSolutionDeveloper(
            @PathVariable String solutionId,
            @RequestBody SolutionUpdateDTO updateDTO) {
        return ResponseEntity.ok(solutionService.updateSolutionDeveloper(solutionId, updateDTO));
    }
}