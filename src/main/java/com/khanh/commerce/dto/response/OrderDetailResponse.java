package com.khanh.commerce.dto.response;

public record OrderDetailResponse(
        Long productId,
        String productName,
        Float price,
        int quantity,
        Float totalMoney
) {
}
