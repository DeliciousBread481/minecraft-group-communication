package com.github.konstantyn111.crashapi.util;

import com.github.konstantyn111.crashapi.exception.BusinessException;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 统一API响应封装类
 *
 * @param <T> 响应数据的类型
 */
@Data
public class ApiResponse<T> {
    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * HTTP状态码
     */
    private int status;

    /**
     * 业务码
     */
    private Integer code;

    /**
     * 给客户端的消息（成功提示或错误信息）
     */
    private String message;

    /**
     * 返回的业务数据
     */
    private T data;

    private ApiResponse(boolean success, int status, Integer code, String message, T data) {
        this.success = success;
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应（包含数据）
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, HttpStatus.OK.value(),
                ErrorCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 创建成功响应（不包含数据）
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(null, message);
    }

    /**
     * 创建失败响应（使用HTTP状态码和错误码）
     */
    public static <T> ApiResponse<T> fail(int httpStatus, ErrorCode errorCode, String message) {
        return new ApiResponse<>(false, httpStatus,
                errorCode.getCode(),
                message != null ? message : errorCode.getMessage(),
                null);
    }

    /**
     * 创建失败响应（使用HTTP状态码和错误码，带自定义消息）
     */
    public static <T> ApiResponse<T> fail(int httpStatus, ErrorCode errorCode) {
        return fail(httpStatus, errorCode, null);
    }

    /**
     * 创建失败响应（使用业务异常）
     */
    public static <T> ApiResponse<T> fail(BusinessException ex) {
        return new ApiResponse<>(false, ex.getHttpStatus().value(),
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                null);
    }
}