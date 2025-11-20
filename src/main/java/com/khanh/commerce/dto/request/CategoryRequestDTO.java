package com.khanh.commerce.dto.request;

import jakarta.validation.constraints.NotBlank;


public record CategoryRequestDTO(
        @NotBlank(message = "Category name is required")
        String name
) {
}
