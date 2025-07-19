package com.github.konstantyn111.crashapi.dto;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;
    private boolean enabled;
    private Set<String> roles;
}