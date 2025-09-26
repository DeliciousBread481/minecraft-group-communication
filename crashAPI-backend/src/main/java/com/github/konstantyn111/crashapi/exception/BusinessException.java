package com.github.konstantyn111.crashapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class BusinessException extends RuntimeException {
    /**
     * 错误码（枚举类型）
     */
    private final ErrorCode errorCode;

    /**
     * HTTP响应状态码
     */
    private final HttpStatus httpStatus;

    /**
     * 业务异常构造函数
     * <p>
     * 创建包含错误码、HTTP状态和详细信息的业务异常实例。
     * </p>
     *
     * @param errorCode 错误码枚举值
     * @param httpStatus HTTP响应状态码
     * @param message 自定义错误消息（可包含具体错误详情）
     */
    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}