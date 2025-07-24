package com.github.konstantyn111.crashapi.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminApplication {
    private Long id;
    private Long userId;
    private String status; // PENDING, APPROVED, REJECTED
    private String reason; // 申请理由
    private Long processorId; // 处理人ID
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    // 关联用户信息（非数据库字段）
    private User user;

    // 关联处理人信息（非数据库字段）
    private User processor;
}