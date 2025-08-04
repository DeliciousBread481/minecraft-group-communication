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

/**
 * 全局异常处理器
 * <p>
 * 统一处理应用程序中的各类异常，返回标准化的错误响应。
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务逻辑异常
     * <p>
     * 捕获BusinessException并返回包含错误码和自定义消息的响应。
     * 使用异常中定义的HTTP状态码构建响应。
     * </p>
     *
     * @param ex 业务逻辑异常实例
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RestResponse<?>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(RestResponse.fail(ex));
    }

    /**
     * 处理凭证错误异常
     * <p>
     * 捕获BadCredentialsException（通常由认证失败触发），
     * 返回401状态码和标准化的错误消息。
     * </p>
     *
     * @param ex 凭证错误异常实例
     * @return 包含认证错误信息的响应实体
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
     * <p>
     * 捕获MethodArgumentNotValidException（由@Valid注解触发），
     * 提取所有字段错误信息并组合为单一错误消息。
     * </p>
     *
     * @param ex 参数验证异常实例
     * @return 包含字段错误信息的响应实体
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
     * <p>
     * 捕获DataIntegrityViolationException，分析根因以识别具体约束冲突，
     * 返回409状态码和针对性的错误消息。
     * </p>
     *
     * @param ex 数据库完整性违反异常实例
     * @return 包含冲突错误信息的响应实体
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
     * <p>
     * 捕获所有未被特定处理器处理的异常，返回500状态码和通用错误消息。
     * </p>
     *
     * @param ex 未捕获的异常实例
     * @return 包含通用错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<?>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.UNKNOWN_ERROR,
                        "服务器内部错误"));
    }
}