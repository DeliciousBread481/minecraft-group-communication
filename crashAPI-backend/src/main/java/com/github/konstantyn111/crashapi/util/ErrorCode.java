package com.github.konstantyn111.crashapi.util;

import lombok.Getter;

/**
 * 错误码枚举，定义业务错误码和消息
 */
@Getter
public enum ErrorCode {
    // 通用错误码 (10000-19999)
    SUCCESS(10000, "Success"),
    UNKNOWN_ERROR(10001, "Unknown error"),
    INVALID_PARAMETER(10002, "Invalid parameter"),
    RESOURCE_NOT_FOUND(10003, "Resource not found"),

    // 认证错误码 (20000-29999)
    AUTHENTICATION_FAILED(20000, "Authentication failed"),
    INVALID_TOKEN(20001, "Invalid token"),
    TOKEN_EXPIRED(20002, "Token expired"),
    ACCESS_DENIED(20003, "Access denied"),

    // 用户相关错误码 (30000-39999)
    USER_NOT_FOUND(30000, "User not found"),
    DUPLICATE_USERNAME(30001, "Duplicate username"),
    DUPLICATE_EMAIL(30002, "Duplicate email"),
    INVALID_CREDENTIALS(30003, "Invalid credentials");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}