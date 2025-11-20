package com.khanh.commerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequestDTO(
        @NotBlank(message = "Product name is required")
        String name,

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be >= 0")
        Float price,

        String description,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {
}
