package com.microtakeout.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 */
public record LoginRequest(
    @NotBlank(message = "用户名或邮箱不能为空")
    String usernameOrEmail,
    
    @NotBlank(message = "密码不能为空")
    String password
) {
}

