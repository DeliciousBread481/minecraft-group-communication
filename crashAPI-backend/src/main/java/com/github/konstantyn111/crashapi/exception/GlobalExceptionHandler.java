package com.github.konstantyn111.crashapi.exception;

import com.github.konstantyn111.crashapi.util.RestResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务逻辑异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RestResponse<?>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(RestResponse.fail(ex));
    }

    /**
     * 处理凭证错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestResponse<?>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RestResponse.fail(HttpStatus.UNAUTHORIZED.value(),
                        ErrorCode.INVALID_CREDENTIALS,
                        "用户名或密码错误"));
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.fail(HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.INVALID_PARAMETER,
                        errorMsg));
    }

    /**
     * 处理数据库唯一约束异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestResponse<?>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getRootCause();
        String errorMsg = "数据库操作失败";

        if (rootCause != null && rootCause.getMessage().contains("unique_email")) {
            errorMsg = "邮箱已被使用";
        } else if (rootCause != null && rootCause.getMessage().contains("unique_username")) {
            errorMsg = "用户名已被使用";
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(RestResponse.fail(HttpStatus.CONFLICT.value(),
                        ErrorCode.DUPLICATE_EMAIL,
                        errorMsg));
    }

    /**
     * 处理其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<?>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.UNKNOWN_ERROR,
                        "服务器内部错误"));
    }
}