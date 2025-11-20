package com.khanh.commerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequestDTO(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "Address is required")
        String address,

        String email,

        String note,

        @NotEmpty(message = "Cart cannot be empty")
        List<CartItemDTO> cartItems // Danh sách sản phẩm
) {
}
