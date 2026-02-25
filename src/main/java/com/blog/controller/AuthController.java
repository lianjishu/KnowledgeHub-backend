package com.blog.controller;

import com.blog.common.ApiResponse;
import com.blog.dto.request.ChangePasswordRequest;
import com.blog.dto.request.UserLoginRequest;
import com.blog.dto.response.LoginResponse;
import com.blog.dto.response.UserResponse;
import com.blog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        return ApiResponse.success(response, "登录成功");
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse.UserInfo> register(@Valid @RequestBody UserLoginRequest request) {
        if (request.getPassword().length() < 6) {
            return ApiResponse.error(400, "密码长度至少6位");
        }
        LoginResponse.UserInfo response = authService.register(request.getUsername(), request.getPassword());
        return ApiResponse.success(response, "注册成功");
    }

    @GetMapping("/info")
    public ApiResponse<UserResponse> getUserInfo(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        UserResponse response = authService.getUserInfo(userId);
        return ApiResponse.success(response);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                              Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        if (request.getNewPassword().length() < 6) {
            return ApiResponse.error(400, "新密码长度至少6位");
        }
        authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ApiResponse.success(null, "密码修改成功");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null, "退出登录成功");
    }
}
