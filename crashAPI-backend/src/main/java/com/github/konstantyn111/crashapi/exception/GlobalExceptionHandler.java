package com.github.konstantyn111.crashapi.exception;

import com.github.konstantyn111.crashapi.util.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一处理应用程序中的各类异常，返回标准化的错误响应。
 * </p>
 */
@Slf4j
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
        log.warn("用户认证失败: {}", ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("loginAttempts", "请检查用户名和密码是否正确");
        details.put("suggestion", "如连续失败多次，请联系管理员重置密码");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RestResponse.fail(HttpStatus.UNAUTHORIZED.value(),
                        ErrorCode.INVALID_CREDENTIALS,
                        "用户名或密码错误，请仔细检查后重新输入",
                        details));
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
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    String fieldName = fieldError.getField();
                    String friendlyName = getFriendlyFieldName(fieldName);
                    String message = fieldError.getDefaultMessage();
                    return friendlyName + ": " + message;
                })
                .collect(Collectors.joining("; "));

        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", fieldErrors);
        details.put("suggestion", "请检查表单中标记为红色的字段");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.fail(HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.INVALID_PARAMETER,
                        "输入信息有误: " + errorMsg,
                        details));
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
        log.warn("数据库完整性约束违反: {}", ex.getMessage());
        Throwable rootCause = ex.getRootCause();
        String errorMsg = "数据保存失败";
        String suggestion = "请检查输入数据是否重复";
        String conflictField = null;

        if (rootCause != null) {
            String message = rootCause.getMessage().toLowerCase();
            if (message.contains("unique_email") || message.contains("email")) {
                errorMsg = "该邮箱地址已被注册";
                suggestion = "请使用其他邮箱地址，或尝试找回密码";
                conflictField = "email";
            } else if (message.contains("unique_username") || message.contains("username")) {
                errorMsg = "该用户名已被使用";
                suggestion = "请选择其他用户名";
                conflictField = "username";
            } else if (message.contains("foreign key")) {
                errorMsg = "引用的数据不存在";
                suggestion = "请检查关联的数据是否正确";
            }
        }

        Map<String, Object> details = new HashMap<>();
        details.put("conflictField", conflictField);
        details.put("suggestion", suggestion);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(RestResponse.fail(HttpStatus.CONFLICT.value(),
                        ErrorCode.DUPLICATE_EMAIL,
                        errorMsg,
                        details));
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
    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<RestResponse<?>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("文件上传大小超限: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("maxSize", ex.getMaxUploadSize());
        details.put("suggestion", "请选择较小的文件或联系管理员增加上传限制");
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(RestResponse.fail(HttpStatus.PAYLOAD_TOO_LARGE.value(),
                        ErrorCode.FILE_SIZE_EXCEEDED,
                        "上传文件过大，请选择小于限制的文件",
                        details));
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestResponse<?>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("权限不足: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("requiredRole", "需要更高的权限级别");
        details.put("suggestion", "请联系管理员申请相应权限，或使用有权限的账号登录");
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RestResponse.fail(HttpStatus.FORBIDDEN.value(),
                        ErrorCode.ACCESS_DENIED,
                        "您没有权限执行此操作",
                        details));
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
        log.error("服务器内部错误: {}", ex.getMessage(), ex);
        
        Map<String, Object> details = new HashMap<>();
        details.put("errorId", System.currentTimeMillis()); // 用于用户反馈时追踪错误
        details.put("timestamp", System.currentTimeMillis());
        details.put("suggestion", "请稍后重试，如问题持续存在请联系管理员并提供错误ID");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.UNKNOWN_ERROR,
                        "服务器遇到了意外错误，我们正在努力修复",
                        details));
    }

    /**
     * 将字段名转换为用户友好的名称
     */
    private String getFriendlyFieldName(String fieldName) {
        Map<String, String> friendlyNames = new HashMap<>();
        friendlyNames.put("username", "用户名");
        friendlyNames.put("email", "邮箱地址");
        friendlyNames.put("password", "密码");
        friendlyNames.put("nickname", "昵称");
        friendlyNames.put("oldPassword", "原密码");
        friendlyNames.put("newPassword", "新密码");
        friendlyNames.put("confirmPassword", "确认密码");
        friendlyNames.put("title", "标题");
        friendlyNames.put("content", "内容");
        friendlyNames.put("categoryId", "分类");
        
        return friendlyNames.getOrDefault(fieldName, fieldName);
    }
}