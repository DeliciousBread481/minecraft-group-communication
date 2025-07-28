package com.github.konstantyn111.crashapi.util.admin;

import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.entity.solution.SolutionImage;
import com.github.konstantyn111.crashapi.entity.solution.SolutionStep;
import com.github.konstantyn111.crashapi.mapper.solution.CategoryMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionImageMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionStepMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SolutionConvertUtil {

    private static CategoryMapper categoryMapper;
    public static SolutionStepMapper solutionStepMapper;
    public static SolutionImageMapper solutionImageMapper;

    @Autowired
    public SolutionConvertUtil(
            CategoryMapper categoryMapper,
            SolutionStepMapper solutionStepMapper,
            SolutionImageMapper solutionImageMapper
    ) {
        SolutionConvertUtil.categoryMapper = categoryMapper;
        SolutionConvertUtil.solutionStepMapper = solutionStepMapper;
        SolutionConvertUtil.solutionImageMapper = solutionImageMapper;
    }

    /**
     * 将Solution实体转换为DTO
     */
    public static SolutionDTO convertToSolutionDTO(Solution solution) {
        if (solution == null) {
            return null;
        }

        SolutionDTO dto = new SolutionDTO();
        dto.setId(solution.getId());
        dto.setCategoryId(solution.getCategoryId());
        dto.setTitle(solution.getTitle());
        dto.setDifficulty(solution.getDifficulty());
        dto.setVersion(solution.getVersion());
        dto.setDescription(solution.getDescription());
        dto.setNotes(solution.getNotes());
        dto.setStatus(solution.getStatus());
        dto.setCreatedAt(solution.getCreatedAt());
        dto.setUpdatedAt(solution.getUpdatedAt());

        // 获取分类信息
        categoryMapper.findById(solution.getCategoryId())
                .ifPresent(category -> dto.setCategoryName(category.getName()));

        // 获取步骤
        if (solutionStepMapper != null) {
            List<SolutionStep> steps = solutionStepMapper.findBySolutionId(solution.getId());
            dto.setSteps(steps.stream()
                    .map(SolutionStep::getContent)
                    .collect(Collectors.toList()));
        }

        // 获取图片
        if (solutionImageMapper != null) {
            List<SolutionImage> images = solutionImageMapper.findBySolutionId(solution.getId());
            dto.setImageUrls(images.stream()
                    .map(SolutionImage::getImageUrl)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * 将Solution实体列表转换为DTO列表
     */
    public static List<SolutionDTO> convertToSolutionDTOList(List<Solution> solutions) {
        return solutions.stream()
                .map(SolutionConvertUtil::convertToSolutionDTO)
                .collect(Collectors.toList());
    }
}