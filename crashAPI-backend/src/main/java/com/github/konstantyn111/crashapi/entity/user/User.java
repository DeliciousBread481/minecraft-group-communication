package com.github.konstantyn111.crashapi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiry;

    public User(String username, String email,String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
