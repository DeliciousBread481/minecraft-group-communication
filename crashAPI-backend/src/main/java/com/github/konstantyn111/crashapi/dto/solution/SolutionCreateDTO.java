package com.github.konstantyn111.crashapi.dto.solution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionCreateDTO {
    private String categoryId;
    private String title;
    private String difficulty;
    private String version;
    private String description;
    private String notes;
    private List<String> steps;
    private List<MultipartFile> imageFiles;
}