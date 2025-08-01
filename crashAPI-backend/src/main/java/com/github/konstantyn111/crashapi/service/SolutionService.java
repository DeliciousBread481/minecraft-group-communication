package com.github.konstantyn111.crashapi.service;

import com.github.konstantyn111.crashapi.dto.solution.CategoryDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.entity.solution.*;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.solution.*;
import com.github.konstantyn111.crashapi.util.ErrorCode;
import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private static final Logger logger = LoggerFactory.getLogger(SolutionService.class);

    private final SolutionMapper solutionMapper;
    private final SolutionStepMapper solutionStepMapper;
    private final SolutionImageMapper solutionImageMapper;
    private final CategoryMapper categoryMapper;

    /**
     * 分页获取已发布的解决方案
     */
    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getPublishedSolutions(Pageable pageable, String categoryId) {
        try {
            List<Solution> solutions = solutionMapper.findPublishedSolutions(
                    SolutionStatus.PUBLISHED,
                    categoryId,
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );

            long total = solutionMapper.countPublishedSolutions(
                    SolutionStatus.PUBLISHED,
                    categoryId
            );

            List<SolutionDTO> dtos = solutions.stream()
                    .map(this::enrichSolutionWithDetails)
                    .collect(Collectors.toList());

            Page<SolutionDTO> page = new PageImpl<>(dtos, pageable, total);
            return RestResponse.success(page, "获取解决方案列表成功");
        } catch (Exception ex) {
            logger.error("获取解决方案列表失败", ex);
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案列表失败: " + ex.getMessage());
        }
    }

    /**
     * 根据ID获取已发布的解决方案详情
     */
    @Transactional(readOnly = true)
    public RestResponse<SolutionDTO> getSolutionById(String solutionId) {
        try {
            Solution solution = solutionMapper.findPublishedById(solutionId, SolutionStatus.PUBLISHED)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "解决方案不存在或未发布"));

            SolutionDTO dto = enrichSolutionWithDetails(solution);
            return RestResponse.success(dto, "获取解决方案成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            logger.error("获取解决方案详情失败: solutionId={}", solutionId, ex);
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 获取所有问题分类
     */
    @Transactional(readOnly = true)
    public RestResponse<List<CategoryDTO>> getAllCategories() {
        try {
            List<Category> categories = categoryMapper.findAll();

            List<CategoryDTO> dtos = categories.stream()
                    .map(category -> {
                        CategoryDTO dto = new CategoryDTO();
                        dto.setId(category.getId());
                        dto.setName(category.getName());
                        dto.setIcon(category.getIcon());
                        dto.setDescription(category.getDescription());
                        dto.setColor(category.getColor());
                        return dto;
                    })
                    .collect(Collectors.toList());

            return RestResponse.success(dtos, "获取分类列表成功");
        } catch (Exception ex) {
            logger.error("获取分类列表失败", ex);
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取分类列表失败: " + ex.getMessage());
        }
    }

    /**
     * 丰富解决方案详情（添加步骤和图片）
     */
    private SolutionDTO enrichSolutionWithDetails(Solution solution) {
        SolutionDTO dto = new SolutionDTO();

        // 获取步骤和图片数据
        List<SolutionStep> steps = solutionStepMapper.findBySolutionId(solution.getId());
        List<SolutionImage> images = solutionImageMapper.findBySolutionId(solution.getId());

        // 设置基本字段
        dto.setId(solution.getId());
        dto.setTitle(solution.getTitle());
        dto.setDifficulty(solution.getDifficulty());
        dto.setVersion(solution.getVersion());
        dto.setDescription(solution.getDescription());
        dto.setNotes(solution.getNotes());
        dto.setStatus(solution.getStatus());
        dto.setCreatedAt(solution.getCreatedAt());
        dto.setUpdatedAt(solution.getUpdatedAt());

        // 格式化更新时间
        if (solution.getUpdatedAt() != null) {
            dto.setUpdateTime(solution.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            logger.warn("解决方案更新时间为空: solutionId={}", solution.getId());
            dto.setUpdateTime("未知时间");
        }

        // 添加解决方案步骤
        dto.setSteps(steps.stream()
                .filter(step -> step != null && step.getStepOrder() != null) // 过滤空值
                .sorted(Comparator.comparing(SolutionStep::getStepOrder))
                .map(SolutionStep::getContent)
                .collect(Collectors.toList()));

        // 记录无效步骤
        steps.stream()
                .filter(step -> step == null || step.getStepOrder() == null)
                .forEach(step -> {
                    if (step == null) {
                        logger.warn("发现空步骤记录: solutionId={}", solution.getId());
                    } else {
                        logger.warn("发现步骤顺序为空的记录: solutionId={}, stepId={}",
                                solution.getId(), step.getId());
                    }
                });

        // 添加解决方案图片 - 添加空值检查
        dto.setImageUrls(images.stream()
                .filter(image -> image != null && image.getImageOrder() != null) // 过滤空值
                .sorted(Comparator.comparing(SolutionImage::getImageOrder))
                .map(SolutionImage::getImageUrl)
                .collect(Collectors.toList()));

        // 记录无效图片
        images.stream()
                .filter(image -> image == null || image.getImageOrder() == null)
                .forEach(image -> {
                    if (image == null) {
                        logger.warn("发现空图片记录: solutionId={}", solution.getId());
                    } else {
                        logger.warn("发现图片顺序为空的记录: solutionId={}, imageId={}",
                                solution.getId(), image.getId());
                    }
                });

        // 添加分类信息 - 添加空值检查
        categoryMapper.findById(solution.getCategoryId())
                .ifPresentOrElse(
                        category -> {
                            dto.setCategoryId(category.getId());
                            dto.setCategoryName(category.getName());
                        },
                        () -> {
                            logger.warn("未找到分类信息: solutionId={}, categoryId={}",
                                    solution.getId(), solution.getCategoryId());
                            dto.setCategoryName("未知分类");
                        }
                );

        return dto;
    }

    // 解决方案状态常量（使用中文）
    private static class SolutionStatus {
        public static final String PUBLISHED = "已发布";
    }
}