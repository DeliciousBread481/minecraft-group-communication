package com.github.konstantyn111.crashapi.util.solution;

import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.entity.solution.*;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.solution.CategoryMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionImageMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionStepMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SolutionUtils {

    public static final String DRAFT = "草稿";
    public static final String PENDING_REVIEW = "待审核";
    public static final String PUBLISHED = "已发布";

    public enum StateAction {
        SUBMIT_FOR_REVIEW, APPROVE, REJECT, WITHDRAW
    }

    private static CategoryMapper categoryMapper;
    private static SolutionStepMapper solutionStepMapper;
    private static SolutionImageMapper solutionImageMapper;

    @Autowired
    public void setMappers(CategoryMapper catMapper, SolutionStepMapper stepMapper, SolutionImageMapper imgMapper) {
        categoryMapper = catMapper;
        solutionStepMapper = stepMapper;
        solutionImageMapper = imgMapper;
    }

    public static String generateId() {
        return "s" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    public static void transitionState(Solution solution, StateAction action, String reason) {
        switch (action) {
            case SUBMIT_FOR_REVIEW -> {
                validateStatus(solution, DRAFT, "提交审核");
                solution.setStatus(PENDING_REVIEW);
            }
            case APPROVE -> {
                validateStatus(solution, PENDING_REVIEW, "批准发布");
                solution.setStatus(PUBLISHED);
            }
            case REJECT -> {
                validateStatus(solution, PENDING_REVIEW, "拒绝");
                solution.setStatus(DRAFT);
                solution.setNotes(reason != null ? reason : "解决方案不符合格式");
            }
            case WITHDRAW -> {
                validateStatus(solution, PUBLISHED, "撤回");
                solution.setStatus(DRAFT);
            }
            default -> throw new BusinessException(ErrorCode.INVALID_OPERATION,
                    HttpStatus.BAD_REQUEST, "无效的状态操作");
        }
    }

    private static void validateStatus(Solution solution, String requiredStatus, String actionName) {
        if (!requiredStatus.equals(solution.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                    HttpStatus.BAD_REQUEST, "无法" + actionName + "，当前状态必须是" + requiredStatus);
        }
    }

    public static SolutionDTO convertToDTO(Solution solution) {
        if (solution == null) return null;

        List<String> steps = solutionStepMapper.findStepsBySolutionId(solution.getId())
                .stream()
                .sorted(Comparator.comparing(SolutionStep::getStepOrder))
                .map(SolutionStep::getContent)
                .collect(Collectors.toList());

        List<String> images = solutionImageMapper.findImagesBySolutionId(solution.getId())
                .stream()
                .sorted(Comparator.comparing(SolutionImage::getImageOrder))
                .map(SolutionImage::getImageUrl)
                .collect(Collectors.toList());

        String categoryName = null;
        if (solution.getCategory() != null) {
            categoryName = solution.getCategory().getName();
        } else if (solution.getCategoryId() != null) {
            categoryName = categoryMapper.findById(solution.getCategoryId())
                    .map(Category::getName)
                    .orElse(null);
        }

        return SolutionDTO.builder()
                .id(solution.getId())
                .title(solution.getTitle())
                .categoryId(solution.getCategoryId())
                .categoryName(categoryName)
                .difficulty(solution.getDifficulty())
                .version(solution.getVersion())
                .updateTime(solution.getUpdatedAt())
                .description(solution.getDescription())
                .notes(solution.getNotes())
                .steps(steps)
                .images(images)
                .createdByUsername(solution.getCreatedByUser() != null ? solution.getCreatedByUser().getUsername() : null)
                .build();
    }

    public static List<SolutionDTO> convertListToDTO(List<Solution> solutions) {
        return solutions.stream().map(SolutionUtils::convertToDTO).collect(Collectors.toList());
    }
}
