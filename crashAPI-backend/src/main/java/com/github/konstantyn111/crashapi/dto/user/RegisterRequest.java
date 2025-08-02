package com.github.konstantyn111.crashapi.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "用户名必须介于 3 到 20 个字符之间")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "电子邮件应有效")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 30, message = "密码必须介于 8 到 30 个字符之间")
    private String password;
}