package com.github.konstantyn111.crashapi.entity.solution;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionImage {
    private Integer id;
    private String solutionId;
    private Integer imageOrder;
    private String imageUrl;
}