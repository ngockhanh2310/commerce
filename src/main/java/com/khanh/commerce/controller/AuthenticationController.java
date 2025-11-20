package com.khanh.commerce.controller;

import com.khanh.commerce.dto.auth.AuthRequest;
import com.khanh.commerce.dto.auth.AuthResponse;
import com.khanh.commerce.dto.auth.RegisterRequest;
import com.khanh.commerce.dto.response.ApiResponse;
import com.khanh.commerce.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ApiResponse.<AuthResponse>builder()
                .message("Register successful")
                .data(authService.register(request))
                .build();
    }

    @PostMapping("/authenticate")
    public ApiResponse<AuthResponse> authenticate(
            @RequestBody AuthRequest request
    ) {
        return ApiResponse.<AuthResponse>builder()
                .message("Login successful")
                .data(authService.authenticate(request))
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(HttpServletRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .message("Token refreshed successfully")
                .data(authService.refreshToken(request))
                .build();
    }
}
