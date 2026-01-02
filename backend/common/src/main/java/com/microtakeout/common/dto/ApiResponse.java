package com.microtakeout.common.dto;

import java.time.Instant;

/**
 * 统一API响应DTO
 * 
 * @param <T> 响应数据类型
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    Instant timestamp,
    String code
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data, Instant.now(), "200");
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now(), "200");
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, Instant.now(), "500");
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, message, null, Instant.now(), code);
    }
}

