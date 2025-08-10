package com.github.konstantyn111.crashapi.util.solution;

import com.github.konstantyn111.crashapi.dto.solution.*;
import com.github.konstantyn111.crashapi.entity.solution.*;
import com.github.konstantyn111.crashapi.entity.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class SolutionMapperUtil {

    public static CategoryDTO toCategoryDTO(Category category) {
        if (category == null) return null;
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .description(category.getDescription())
                .color(category.getColor())
                .build();
    }

    public static SolutionDTO toSolutionDTO(Solution solution,
                                            List<String> steps,
                                            List<String> images) {
        if (solution == null) return null;
        return SolutionDTO.builder()
                .id(solution.getId())
                .title(solution.getTitle())
                .categoryId(solution.getCategoryId())
                .categoryName(solution.getCategory() != null ? solution.getCategory().getName() : null)
                .difficulty(solution.getDifficulty())
                .version(solution.getVersion())
                .updateTime(solution.getUpdatedAt())
                .description(solution.getDescription())
                .notes(solution.getNotes())
                .steps(steps != null ? steps : Collections.emptyList())
                .images(images != null ? images : Collections.emptyList())
                .build();
    }

    public static Solution toSolutionEntity(SolutionCreateDTO dto, User admin) {
        return Solution.builder()
                .id(SolutionUtils.generateId())
                .categoryId(dto.getCategoryId())
                .title(dto.getTitle())
                .difficulty(dto.getDifficulty())
                .version(dto.getVersion())
                .description(dto.getDescription())
                .notes(dto.getNotes())
                .status(SolutionUtils.DRAFT)
                .createdBy(admin.getId())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
