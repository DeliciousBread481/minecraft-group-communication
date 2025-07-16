package com.github.konstantyn111.crashapi.util;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 通用API响应封装类，用于统一REST API的响应格式。
 * <p>
 * 该类遵循标准响应结构，包含操作状态、状态码、消息和返回数据。
 * 支持成功和失败两种响应类型，提供静态工厂方法简化创建过程。
 * </p>
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
    private int code;

    /**
     * 给客户端的消息（成功提示或错误信息）
     */
    private String message;

    /**
     * 返回的业务数据（成功时通常包含数据，失败时可能为null）
     */
    private T data;

    /**
     * 私有构造函数，强制使用静态工厂方法创建实例。
     *
     * @param success 操作是否成功
     * @param code HTTP状态码
     * @param message 响应消息
     * @param data 业务数据
     */
    private ApiResponse(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应（包含数据）。
     * <p>
     * 默认使用200(OK)状态码。
     * </p>
     *
     * @param data 要返回的业务数据
     * @param message 成功提示消息
     * @param <T> 业务数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, HttpStatus.OK.value(), message, data);
    }

    /**
     * 创建成功响应（不包含数据）。
     * <p>
     * 默认使用200(OK)状态码。
     * </p>
     *
     * @param message 成功提示消息
     * @param <T> 业务数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, HttpStatus.OK.value(), message, null);
    }

    /**
     * 创建失败响应（包含数据）。
     * <p>
     * 用于需要返回额外错误数据的场景。
     * </p>
     *
     * @param code HTTP错误状态码（如400,404,500等）
     * @param message 错误描述信息
     * @param data 错误相关的额外数据
     * @param <T> 错误数据类型
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> fail(int code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    /**
     * 创建失败响应（不包含数据）。
     * <p>
     * 最常见的失败响应形式。
     * </p>
     *
     * @param code HTTP错误状态码（如400,404,500等）
     * @param message 错误描述信息
     * @param <T> 业务数据类型
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> fail(int code, String message) {
        return fail(code, message, null);
    }
}