package com.khanh.commerce.dto.auth;

// DTO Login
public record AuthRequest(
        String username,
        String password
) {
}
