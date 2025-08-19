package com.github.konstantyn111.crashapi.dto.solution;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionDTO {
    private String id;
    private String title;
    private String categoryId;
    private String categoryName;
    private String difficulty;
    private String version;
    private LocalDateTime updateTime;
    private String description;
    private List<String> steps;
    private String notes;
    private List<String> images;
    private String createdByUsername;
}