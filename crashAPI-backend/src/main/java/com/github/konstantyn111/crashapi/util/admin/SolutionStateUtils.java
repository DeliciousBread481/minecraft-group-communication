package com.github.konstantyn111.crashapi.util.admin;

import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 解决方案状态管理
 */
public class SolutionStateUtils {
    //四个状态的枚举
    public enum SolutionStateAction {
        //写个操作流程当备忘录了
        SUBMIT_FOR_REVIEW,   // 提交审核（草稿 → 待审核）
        APPROVE,             // 批准发布（待审核 → 已发布）
        REJECT,              // 拒绝（待审核 → 草稿）
        WITHDRAW             // 撤回（已发布 → 草稿）
    }

    /**
     * 执行状态的转换
     * @param solution 解决方案实体
     * @param action 状态操作
     * @param reason 拒绝原因 REJECT要！其他的别传这个参数
     * @return 更新后的解决方案
     */
    public static Solution transitionState(Solution solution, SolutionStateAction action, String reason) {
        switch (action) {
            case SUBMIT_FOR_REVIEW:
                validateCurrentStatus(solution, SolutionStatus.DRAFT, "提交审核");
                solution.setStatus(SolutionStatus.PENDING_REVIEW);
                break;

            case APPROVE:
                validateCurrentStatus(solution, SolutionStatus.PENDING_REVIEW, "批准发布");
                solution.setStatus(SolutionStatus.PUBLISHED);
                break;

            case REJECT:
                validateCurrentStatus(solution, SolutionStatus.PENDING_REVIEW, "拒绝");
                solution.setStatus(SolutionStatus.DRAFT);
                solution.setNotes(reason != null ? reason : "解决方案不符合格式");
                break;

            case WITHDRAW:
                validateCurrentStatus(solution, SolutionStatus.PUBLISHED, "撤回");
                solution.setStatus(SolutionStatus.DRAFT);
                break;

            default:
                throw new BusinessException(ErrorCode.INVALID_OPERATION,
                        HttpStatus.BAD_REQUEST, "无效的状态操作");
        }

        return solution;
    }

    /**
     * 验证解决方案状态
     * @param solution 解决方案实体
     * @param requiredStatus 解决方案状态
     * @param actionName 操作文本
     */
    private static void validateCurrentStatus(Solution solution, String requiredStatus, String actionName) {
        if (!requiredStatus.equals(solution.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                    HttpStatus.BAD_REQUEST,
                    "无法" + actionName + "，当前状态必须是" + requiredStatus);
        }
    }
}