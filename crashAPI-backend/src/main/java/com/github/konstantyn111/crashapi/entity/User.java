package com.github.konstantyn111.crashapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;
    private boolean enabled;
    private Collection<Role> roles = new ArrayList<>();
    private String refreshToken;
    private Date refreshTokenExpiry;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.enabled = true;
        this.roles = new ArrayList<>();
    }
}