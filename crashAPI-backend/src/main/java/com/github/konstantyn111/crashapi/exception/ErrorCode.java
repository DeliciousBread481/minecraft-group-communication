package com.github.konstantyn111.crashapi.exception;

import lombok.Getter;

/**
 * 业务错误码枚举
 */
@Getter
public enum ErrorCode {
    // ===================== 通用错误码 (10000-19999) =====================
    /** 操作成功 */
    SUCCESS(10000, "操作成功"),

    /** 未知系统错误 */
    UNKNOWN_ERROR(10001, "未知系统错误"),

    /** 请求参数不合法 */
    INVALID_PARAMETER(10002, "请求参数不合法"),

    /** 请求资源不存在 */
    RESOURCE_NOT_FOUND(10003, "请求资源不存在"),

    /** 服务器内部处理错误 */
    INTERNAL_SERVER_ERROR(10004, "服务器内部错误"),

    /** 接口功能未实现 */
    FEATURE_NOT_IMPLEMENTED(10005, "功能尚未实现"),

    /** 请求体格式错误 */
    INVALID_REQUEST(10006, "请求体格式错误"),

    /** 数据库操作失败 */
    DATABASE_OPERATION_FAILED(10007, "数据库操作失败"),

    /** 数据验证失败 */
    DATA_VALIDATION_FAILED(10008, "数据验证失败"),

    // ===================== 认证错误码 (20000-29999) =====================
    /** 用户认证失败 */
    AUTHENTICATION_FAILED(20000, "用户认证失败"),

    /** 无效的令牌 */
    INVALID_TOKEN(20001, "无效的令牌"),

    /** 令牌已过期 */
    TOKEN_EXPIRED(20002, "令牌已过期"),

    /** 访问权限不足 */
    ACCESS_DENIED(20003, "访问权限不足"),

    /** 刷新令牌无效 */
    INVALID_REFRESH_TOKEN(20004, "刷新令牌无效"),

    /** 刷新令牌已过期 */
    REFRESH_TOKEN_EXPIRED(20005, "刷新令牌已过期"),

    // ===================== 用户相关错误码 (30000-39999) =====================
    /** 用户不存在 */
    USER_NOT_FOUND(30000, "用户不存在"),

    /** 角色不存在 */
    ROLE_NOT_FOUND(30001, "角色不存在"),

    /** 用户名重复 */
    DUPLICATE_USERNAME(30002, "用户名已被使用"),

    /** 邮箱已被注册 */
    DUPLICATE_EMAIL(30003, "邮箱已被注册"),

    /** 用户凭证错误 */
    INVALID_CREDENTIALS(30004, "用户名或密码错误"),

    /** 未授权的操作 */
    UNAUTHORIZED(30005, "未授权的操作"),

    /** 管理员申请记录不存在 */
    APPLICATION_NOT_FOUND(30006, "管理员申请记录不存在"),

    /** 申请状态无效 */
    INVALID_APPLICATION_STATUS(30007, "申请状态无效"),

    /** 操作失败 */
    OPERATION_FAILED(30008, "操作失败"),

    /** 用户已是管理员 */
    ALREADY_ADMIN(30009, "用户已是管理员"),

    /** 已存在待处理的申请 */
    PENDING_APPLICATION_EXISTS(30010, "已存在待处理的申请"),

    /** 权限不足 */
    PERMISSION_DENIED(30011, "权限不足"),

    /** 用户账户未启用 */
    USER_DISABLED(30012, "用户账户未启用"),

    /** 用户角色分配失败 */
    ROLE_ASSIGNMENT_FAILED(30013, "用户角色分配失败"),

    /** 用户角色移除失败 */
    ROLE_REMOVAL_FAILED(30014, "用户角色移除失败"),

    /** 刷新令牌生成失败 */
    REFRESH_TOKEN_GENERATION_FAILED(30015, "刷新令牌生成失败"),

    // ===================== 文件相关错误码 (40000-49999) =====================
    /** 文件处理失败 */
    FILE_UPLOAD_FAILED(40000, "文件上传失败"),

    /** 文件大小超限 */
    FILE_SIZE_EXCEEDED(40001, "文件大小超过限制"),

    /** 文件类型不支持 */
    UNSUPPORTED_FILE_TYPE(40002, "文件类型不支持"),

    /** 文件下载失败 */
    FILE_DOWNLOAD_FAILED(40003, "文件下载失败"),

    /** 文件删除失败 */
    FILE_DELETION_FAILED(40004, "文件删除失败"),

    /** 文件不存在 */
    FILE_NOT_FOUND(40005, "文件不存在"),

    INVALID_FILE_PATH(40006,"非法文件路径"),

    FILE_STORAGE_ERROR(40007,"文件存储失败"),

    // ===================== 解决方案相关错误码 (50000-59999) =====================
    /** 问题分类不存在 */
    CATEGORY_NOT_FOUND(50000, "问题分类不存在"),

    /** 解决方案不存在 */
    SOLUTION_NOT_FOUND(50001, "解决方案不存在"),

    /** 解决方案状态无效 */
    INVALID_SOLUTION_STATUS(50002, "解决方案状态无效"),

    /** 解决方案创建失败 */
    SOLUTION_CREATION_FAILED(50003, "解决方案创建失败"),

    /** 解决方案更新失败 */
    SOLUTION_UPDATE_FAILED(50004, "解决方案更新失败"),

    /** 解决方案删除失败 */
    SOLUTION_DELETION_FAILED(50005, "解决方案删除失败"),

    /** 解决方案提交审核失败 */
    SOLUTION_REVIEW_SUBMISSION_FAILED(50006, "解决方案提交审核失败"),

    /** 解决方案审核失败 */
    SOLUTION_REVIEW_FAILED(50007, "解决方案审核失败"),

    /** 解决方案步骤无效 */
    INVALID_SOLUTION_STEP(50008, "解决方案步骤无效"),

    /** 解决方案图片无效 */
    INVALID_SOLUTION_IMAGE(50009, "解决方案图片无效"),

    /** 解决方案版本不兼容 */
    SOLUTION_VERSION_INCOMPATIBLE(50010, "解决方案版本不兼容"),

    /** 解决方案步骤顺序冲突 */
    SOLUTION_STEP_ORDER_CONFLICT(50011, "解决方案步骤顺序冲突"),

    /** 解决方案图片顺序冲突 */
    SOLUTION_IMAGE_ORDER_CONFLICT(50012, "解决方案图片顺序冲突"),

    /** 解决方案分类不匹配 */
    SOLUTION_CATEGORY_MISMATCH(50013, "解决方案分类不匹配"),

    /** 无效的状态操作 */
    INVALID_OPERATION(50014, "无效的状态操作"),;

    /** 业务错误码 */
    private final int code;

    /** 默认错误提示信息（中文） */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}