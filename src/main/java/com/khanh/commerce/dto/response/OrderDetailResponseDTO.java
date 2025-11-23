package com.khanh.commerce.dto.response;

public record OrderDetailResponseDTO(
        Long productId,
        String productName,
        Float price,
        int quantity,
        Float totalMoney
) {
}
