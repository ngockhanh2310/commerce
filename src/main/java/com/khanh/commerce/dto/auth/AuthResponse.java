package com.khanh.commerce.dto.auth;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.khanh.commerce.model.Role;

// DTO response token
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String username,
        Role role,
        String email,
        String fullName,
        String address,
        String phoneNumber,
        String token,
        String refreshToken
) {
}