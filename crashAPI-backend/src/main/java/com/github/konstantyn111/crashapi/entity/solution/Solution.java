package com.github.konstantyn111.crashapi.entity.solution;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solution {
    private String id;
    private String categoryId;
    @Setter
    @Getter
    private Category category;
    private String title;
    private String difficulty;
    private String version;
    private String description;
    private String notes;
    private String status; // 状态：草稿、待审核、已发布
    private Long createdBy;
    private Long reviewedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}