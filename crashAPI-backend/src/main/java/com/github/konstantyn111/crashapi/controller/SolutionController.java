package com.github.konstantyn111.crashapi.controller;

import com.github.konstantyn111.crashapi.dto.solution.CategoryDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.service.SolutionService;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    /**
     * 分页获取已发布的解决方案
     */
    @GetMapping
    public ResponseEntity<RestResponse<Page<SolutionDTO>>> getSolutions(
            Pageable pageable,
            @RequestParam(required = false) String categoryId) {
        return ResponseEntity.ok(solutionService.getPublishedSolutions(pageable, categoryId));
    }

    /**
     * 根据ID获取解决方案详情
     */
    @GetMapping("/{solutionId}")
    public ResponseEntity<RestResponse<SolutionDTO>> getSolutionById(
            @PathVariable String solutionId) {
        return ResponseEntity.ok(solutionService.getSolutionById(solutionId));
    }

    /**
     * 获取所有问题分类
     */
    @GetMapping("/categories")
    public ResponseEntity<RestResponse<List<CategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(solutionService.getAllCategories());
    }
}