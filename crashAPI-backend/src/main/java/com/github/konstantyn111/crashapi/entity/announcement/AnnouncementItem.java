package com.github.konstantyn111.crashapi.entity.announcement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementItem {
    private Long id;
    private String categoryId;
    private String itemType;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private Long lastUpdatedBy;
}
