package com.github.konstantyn111.crashapi.dto.solution;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionDTO {
    private String id;
    private String categoryId;
    private String categoryName;
    private String title;
    private String difficulty;
    private String version;
    private String description;
    private String notes;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updateTime;
    private List<String> steps;
    private List<String> imageUrls;
}