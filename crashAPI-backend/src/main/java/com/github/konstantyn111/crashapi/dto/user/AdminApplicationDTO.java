package com.github.konstantyn111.crashapi.dto.user;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminApplicationDTO {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String status;
    private String processorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}