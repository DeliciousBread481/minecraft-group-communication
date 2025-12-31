package com.github.konstantyn111.crashapi.controller;

import com.github.konstantyn111.crashapi.util.RestResponse;
import org.springframework.http.ResponseEntity;

/**
 * 基础控制器，统一处理响应格式
 */
public class BaseController {

    /**
     * 返回成功响应
     */
    protected <T> ResponseEntity<RestResponse<T>> success(RestResponse<T> response) {
        return ResponseEntity.ok(response);
    }

    /**
     * 返回成功响应（带数据）
     */
    protected <T> ResponseEntity<RestResponse<T>> success(T data) {
        return ResponseEntity.ok(RestResponse.success(data));
    }

    /**
     * 返回成功响应（带消息和数据）
     */
    protected <T> ResponseEntity<RestResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(RestResponse.success(data, message));
    }

    /**
     * 返回成功响应（仅消息）
     */
    protected ResponseEntity<RestResponse<Void>> success(String message) {
        return ResponseEntity.ok(RestResponse.success(message));
    }

    /**
     * 返回成功响应（无消息无数据）
     */
    protected ResponseEntity<RestResponse<Void>> success() {
        return ResponseEntity.ok(RestResponse.success("操作成功"));
    }
}