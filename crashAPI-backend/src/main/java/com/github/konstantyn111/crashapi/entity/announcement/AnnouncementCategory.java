package com.github.konstantyn111.crashapi.entity.announcement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementCategory {
    private String id;          // 分类ID
    private String name;        // 分类名称
    private Integer sortOrder;  // 排序顺序
    private LocalDateTime createdAt; // 创建时间
}

