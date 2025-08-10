package com.github.konstantyn111.crashapi.entity.solution;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionStep {
    private Long id;
    private String solutionId;
    private Integer stepOrder;
    private String content;
}
