package com.github.konstantyn111.crashapi.dto;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
}