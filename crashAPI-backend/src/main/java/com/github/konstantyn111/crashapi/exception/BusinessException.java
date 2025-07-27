package com.github.konstantyn111.crashapi.exception;

import com.github.konstantyn111.crashapi.util.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务逻辑异常
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}