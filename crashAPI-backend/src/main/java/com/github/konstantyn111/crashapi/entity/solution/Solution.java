package com.github.konstantyn111.crashapi.entity.solution;

import lombok.*;
import java.time.LocalDateTime;
import com.github.konstantyn111.crashapi.entity.user.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solution {
    private String id;
    private String categoryId;
    private Category category;
    private String title;
    private String difficulty;
    private String version;
    private String description;
    private String notes;
    private String status; // 草稿、待审核、已发布
    private Long createdBy;
    private User createdByUser;   // 创建者
    private Long reviewedBy;
    private User reviewedByUser;  // 审核者
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}