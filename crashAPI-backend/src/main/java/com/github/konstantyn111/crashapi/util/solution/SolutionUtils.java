package com.github.konstantyn111.crashapi.util.solution;

import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.entity.solution.*;
import com.github.konstantyn111.crashapi.mapper.solution.*;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解决方案工具类（状态管理、数据转换）
 * <p>
 * 提供解决方案状态管理、数据转换和ID生成等核心功能。
 * </p>
 */
@Component
public class SolutionUtils {
    private static final Logger logger = LoggerFactory.getLogger(SolutionUtils.class);

    // ==================== 状态常量 ====================
    public static final String DRAFT = "草稿";
    public static final String PENDING_REVIEW = "待审核";
    public static final String PUBLISHED = "已发布";

    // ==================== 状态操作枚举 ====================
    public enum StateAction {
        SUBMIT_FOR_REVIEW, // 提交审核（草稿 → 待审核）
        APPROVE,           // 批准发布（待审核 → 已发布）
        REJECT,            // 拒绝（待审核 → 草稿）
        WITHDRAW           // 撤回（已发布 → 草稿）
    }

    // ==================== 依赖注入 ====================
    private static CategoryMapper categoryMapper;
    private static SolutionStepMapper solutionStepMapper;
    private static SolutionImageMapper solutionImageMapper;

    /**
     * 设置解决方案相关数据访问接口
     *
     * @param categoryMapper 分类数据访问接口
     * @param solutionStepMapper 解决方案步骤数据访问接口
     * @param solutionImageMapper 解决方案图片数据访问接口
     */
    @Autowired
    public void setMappers(
            CategoryMapper categoryMapper,
            SolutionStepMapper solutionStepMapper,
            SolutionImageMapper solutionImageMapper
    ) {
        SolutionUtils.categoryMapper = categoryMapper;
        SolutionUtils.solutionStepMapper = solutionStepMapper;
        SolutionUtils.solutionImageMapper = solutionImageMapper;
    }

    // ==================== 状态管理 ====================

    /**
     * 生成唯一解决方案ID
     * <p>
     * 使用时间戳和随机数组合生成全局唯一ID。
     * </p>
     *
     * @return 生成的解决方案ID（格式："s" + 时间戳 + 随机数）
     */
    public static String generateId() {
        return "s" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    /**
     * 执行解决方案状态转换
     * <p>
     * 根据操作类型更新解决方案状态，并验证状态转换的合法性。
     * </p>
     *
     * @param solution 要操作的解决方案实体
     * @param action 状态操作类型
     * @param reason 操作原因（用于拒绝操作）
     * @throws BusinessException 当状态转换不合法时抛出
     */
    public static void transitionState(Solution solution, StateAction action, String reason) {
        switch (action) {
            case SUBMIT_FOR_REVIEW:
                validateStatus(solution, DRAFT, "提交审核");
                solution.setStatus(PENDING_REVIEW);
                break;

            case APPROVE:
                validateStatus(solution, PENDING_REVIEW, "批准发布");
                solution.setStatus(PUBLISHED);
                break;

            case REJECT:
                validateStatus(solution, PENDING_REVIEW, "拒绝");
                solution.setStatus(DRAFT);
                solution.setNotes(reason != null ? reason : "解决方案不符合格式");
                break;

            case WITHDRAW:
                validateStatus(solution, PUBLISHED, "撤回");
                solution.setStatus(DRAFT);
                break;

            default:
                throw new BusinessException(ErrorCode.INVALID_OPERATION,
                        HttpStatus.BAD_REQUEST, "无效的状态操作");
        }
    }

    /**
     * 验证解决方案状态是否符合要求
     * <p>
     * 检查解决方案当前状态是否与要求的操作状态匹配。
     * </p>
     *
     * @param solution 要验证的解决方案实体
     * @param requiredStatus 要求的当前状态
     * @param actionName 操作名称（用于错误信息）
     * @throws BusinessException 当状态不匹配时抛出
     */
    private static void validateStatus(Solution solution, String requiredStatus, String actionName) {
        if (!requiredStatus.equals(solution.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                    HttpStatus.BAD_REQUEST,
                    "无法" + actionName + "，当前状态必须是" + requiredStatus);
        }
    }

    // ==================== 数据转换 ====================

    /**
     * 将解决方案实体转换为数据传输对象
     * <p>
     * 包含基本信息和关联数据（分类、步骤、图片）。
     * </p>
     *
     * @param solution 要转换的解决方案实体
     * @return 解决方案数据传输对象（可能为null）
     */
    public static SolutionDTO convertToDTO(Solution solution) {
        if (solution == null) return null;

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

        categoryMapper.findById(solution.getCategoryId())
                .ifPresent(category -> dto.setCategoryName(category.getName()));

        List<SolutionStep> steps = solutionStepMapper.findBySolutionId(solution.getId());
        dto.setSteps(steps.stream()
                .map(SolutionStep::getContent)
                .collect(Collectors.toList()));

        List<SolutionImage> images = solutionImageMapper.findBySolutionId(solution.getId());
        dto.setImageUrls(images.stream()
                .map(SolutionImage::getImageUrl)
                .collect(Collectors.toList()));

        return dto;
    }

    /**
     * 丰富解决方案详情信息
     * <p>
     * 在基本转换基础上添加格式化时间和排序后的步骤/图片。
     * </p>
     *
     * @param solution 要丰富的解决方案实体
     * @return 包含丰富信息的解决方案DTO
     */
    public SolutionDTO enrichWithDetails(Solution solution) {
        SolutionDTO dto = convertToDTO(solution);
        if (dto == null) return null;

        if (solution.getUpdatedAt() != null) {
            dto.setUpdateTime(solution.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            logger.warn("解决方案更新时间为空: solutionId={}", solution.getId());
            dto.setUpdateTime("未知时间");
        }

        List<SolutionStep> steps = solutionStepMapper.findBySolutionId(solution.getId());
        dto.setSteps(steps.stream()
                .filter(step -> step != null && step.getStepOrder() != null)
                .sorted(Comparator.comparing(SolutionStep::getStepOrder))
                .map(SolutionStep::getContent)
                .collect(Collectors.toList()));

        List<SolutionImage> images = solutionImageMapper.findBySolutionId(solution.getId());
        dto.setImageUrls(images.stream()
                .filter(image -> image != null && image.getImageOrder() != null)
                .sorted(Comparator.comparing(SolutionImage::getImageOrder))
                .map(SolutionImage::getImageUrl)
                .collect(Collectors.toList()));

        return dto;
    }

    /**
     * 将解决方案实体列表转换为DTO列表
     *
     * @param solutions 解决方案实体列表
     * @return 解决方案DTO列表
     */
    public static List<SolutionDTO> convertListToDTO(List<Solution> solutions) {
        return solutions.stream()
                .map(SolutionUtils::convertToDTO)
                .collect(Collectors.toList());
    }
}