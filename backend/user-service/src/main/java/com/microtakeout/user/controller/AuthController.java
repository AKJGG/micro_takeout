package com.microtakeout.user.controller;

import com.microtakeout.common.dto.ApiResponse;
import com.microtakeout.user.dto.LoginRequest;
import com.microtakeout.user.dto.LoginResponse;
import com.microtakeout.user.dto.RegisterRequest;
import com.microtakeout.user.entity.User;
import com.microtakeout.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ApiResponse.success("注册成功", user);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ApiResponse.success("登录成功", response);
    }

    @PostMapping("/verify-email")
    public ApiResponse<Object> verifyEmail(@RequestParam Long userId, @RequestParam String token) {
        userService.verifyEmail(userId, token);
        return ApiResponse.success("邮箱验证成功");
    }

    @PostMapping("/reset-password")
    public ApiResponse<Object> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        userService.resetPassword(email, newPassword);
        return ApiResponse.success("密码重置成功");
    }
}

