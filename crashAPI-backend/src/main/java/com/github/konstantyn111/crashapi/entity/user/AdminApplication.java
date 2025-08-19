package com.github.konstantyn111.crashapi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminApplication {
    private Long id;
    private Long applicantId;
    private String applicantUsername;
    private String status; // PENDING, APPROVED, REJECTED
    private String reason; // 申请理由
    private String feedback; // 审批反馈
    private Long processorId; // 处理人ID
    private String processorName;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}