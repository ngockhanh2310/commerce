package com.khanh.commerce.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequestDTO(
        @NotBlank(message = "Product name is required")
        String name,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be >= 0")
        Float price,
        
        String description,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {
}
