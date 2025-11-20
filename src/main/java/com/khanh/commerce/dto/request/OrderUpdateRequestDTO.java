package com.khanh.commerce.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrderUpdateRequestDTO(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "Address is required")
        String address,

        String note
) {
}