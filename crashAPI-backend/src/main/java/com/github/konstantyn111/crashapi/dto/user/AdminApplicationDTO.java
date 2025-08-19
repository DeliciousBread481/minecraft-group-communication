package com.github.konstantyn111.crashapi.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminApplicationDTO {
    private Long id;
    private String applicantUsername;
    private String status;
    private String reason;
    private String feedback;
    private String processorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
