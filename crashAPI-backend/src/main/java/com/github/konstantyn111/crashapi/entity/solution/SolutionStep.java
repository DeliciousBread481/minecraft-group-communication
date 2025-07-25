package com.github.konstantyn111.crashapi.entity.solution;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionStep {
    private Integer id;
    private String solutionId;
    private Integer stepOrder;
    private String content;
}