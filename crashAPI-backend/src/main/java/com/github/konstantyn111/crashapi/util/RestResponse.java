package com.github.konstantyn111.crashapi.util;

import com.github.konstantyn111.crashapi.exception.BusinessException;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 统一API响应封装类
 * <p>
 * 标准化API响应格式
 *{
    success: bool
    status: int
    code: int
    message: String
    data: obj
 *}
 * @param <T> 响应数据的类型
 */
@Data
public class RestResponse<T> {
    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * HTTP状态码
     */
    private int status;

    /**
     * 业务状态码
     */
    private Integer code;

    /**
     * 消息提示（成功提示或错误信息）
     */
    private String message;

    /**
     * 返回的业务数据
     */
    private T data;

    private RestResponse(boolean success, int status, Integer code, String message, T data) {
        this.success = success;
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应（含数据）
     * @param data 返回的业务数据
     * @param message 成功提示信息
     * @return 标准化成功响应
     */
    public static <T> RestResponse<T> success(T data, String message) {
        return new RestResponse<>(true, HttpStatus.OK.value(),
                ErrorCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 创建成功响应（无数据）
     * @param message 成功提示信息
     * @return 标准化成功响应
     */
    public static <T> RestResponse<T> success(String message) {
        return success(null, message);
    }

    /**
     * 创建失败响应
     * @param httpStatus HTTP状态码
     * @param errorCode 业务错误码
     * @param message 自定义错误信息（为空时使用错误码默认信息）
     * @return 标准化错误响应
     */
    public static <T> RestResponse<T> fail(int httpStatus, ErrorCode errorCode, String message) {
        return new RestResponse<>(false, httpStatus,
                errorCode.getCode(),
                message != null ? message : errorCode.getMessage(),
                null);
    }

    /**
     * 从业务异常创建失败响应
     * @param ex 业务异常实例
     * @return 标准化错误响应
     */
    public static <T> RestResponse<T> fail(BusinessException ex) {
        return new RestResponse<>(false, ex.getHttpStatus().value(),
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                null);
    }
}