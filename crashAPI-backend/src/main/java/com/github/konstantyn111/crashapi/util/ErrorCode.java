package com.github.konstantyn111.crashapi.util;

import lombok.Getter;

/**
 * 业务错误码枚举
 * <p>
 * 标准化错误代码体系，按功能模块分组管理，每个错误码包含唯一编码和默认提示信息
 */
@Getter
public enum ErrorCode {
    // ===================== 通用错误码 (10000-19999) =====================

    /** 操作成功 */
    SUCCESS(10000, "Success"),

    /** 未知系统错误 */
    UNKNOWN_ERROR(10001, "Unknown error"),

    /** 请求参数不合法 */
    INVALID_PARAMETER(10002, "Invalid parameter"),

    /** 请求资源不存在 */
    RESOURCE_NOT_FOUND(10003, "Resource not found"),

    /** 服务器内部处理错误 */
    INTERNAL_SERVER_ERROR(10004, "服务器内部错误"),

    /** 接口功能未实现 */
    FEATURE_NOT_IMPLEMENTED(10005, "功能尚未实现"),

    /** 请求体格式错误 */
    INVALID_REQUEST(10006, "请求体格式错误"),

    // ===================== 认证错误码 (20000-29999) =====================

    /** 用户认证失败 */
    AUTHENTICATION_FAILED(20000, "Authentication failed"),

    /** 无效的令牌 */
    INVALID_TOKEN(20001, "Invalid token"),

    /** 令牌已过期 */
    TOKEN_EXPIRED(20002, "Token expired"),

    /** 访问权限不足 */
    ACCESS_DENIED(20003, "Access denied"),

    // ===================== 用户相关错误码 (30000-39999) =====================

    /** 用户不存在 */
    USER_NOT_FOUND(30000, "User not found"),

    /** 用户名重复 */
    DUPLICATE_USERNAME(30001, "Duplicate username"),

    /** 邮箱已被注册 */
    DUPLICATE_EMAIL(30002, "Duplicate email"),

    /** 用户凭证错误 */
    INVALID_CREDENTIALS(30003, "Invalid credentials"),

    /** 未授权的操作 */
    UNAUTHORIZED(30004, "Unauthorized"),

    // ===================== 文件相关错误码 (40000-49999) =====================

    /** 文件处理失败 */
    FILE_UPLOAD_FAILED(40000, "文件上传失败"),

    /** 文件大小超限 */
    FILE_SIZE_EXCEEDED(40001, "文件大小超过限制");

    /** 业务错误码（全局唯一） */
    private final int code;

    /** 默认错误提示信息 */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}