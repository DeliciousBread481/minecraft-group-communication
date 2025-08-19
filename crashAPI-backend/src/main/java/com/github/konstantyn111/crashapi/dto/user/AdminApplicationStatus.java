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
public class AdminApplicationStatus {
    private Long id;
    private Long applicantId;
    private String status;
    private String reason;
    private String feedback;
    private LocalDateTime createdAt;
}
