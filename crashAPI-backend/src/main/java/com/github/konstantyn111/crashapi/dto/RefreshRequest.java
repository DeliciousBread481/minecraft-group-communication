package com.github.konstantyn111.crashapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}