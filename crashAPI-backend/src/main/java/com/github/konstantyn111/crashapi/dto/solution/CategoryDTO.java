package com.github.konstantyn111.crashapi.dto.solution;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private String id;
    private String name;
    private String icon;
    private String description;
    private String color;
    private String createdByUsername;
}
