package com.microtakeout.user.dto;

/**
 * 登录响应DTO
 */
public record LoginResponse(
    String token,
    Long userId,
    String username,
    String role
) {
}

