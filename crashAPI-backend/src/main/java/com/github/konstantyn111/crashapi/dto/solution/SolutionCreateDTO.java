package com.github.konstantyn111.crashapi.dto.solution;

import lombok.Data;
import java.util.List;

@Data
public class SolutionCreateDTO {
    private String categoryId;
    private String title;
    private String difficulty;
    private String version;
    private String description;
    private String notes;
    private List<String> steps;
    private List<String> imageUrls;
}