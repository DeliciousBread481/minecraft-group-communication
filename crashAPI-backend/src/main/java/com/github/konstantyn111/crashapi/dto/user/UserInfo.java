package com.github.konstantyn111.crashapi.dto.user;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
    private Set<String> roles;
}