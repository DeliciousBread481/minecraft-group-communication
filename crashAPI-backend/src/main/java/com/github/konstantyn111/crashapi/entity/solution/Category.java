package com.github.konstantyn111.crashapi.entity.solution;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private String id;
    private String name;
    private String icon;
    private String description;
    private String color;
    private Long createdBy;
    private LocalDateTime createdAt;
}