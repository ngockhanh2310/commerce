package com.khanh.commerce.dto.response;

import com.khanh.commerce.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        String fullName,
        String phoneNumber,
        String address,
        String note,
        String email,
        LocalDateTime orderDate,
        OrderStatus status,
        Float totalMoney,
        List<OrderDetailResponseDTO> orderDetails
) {
}