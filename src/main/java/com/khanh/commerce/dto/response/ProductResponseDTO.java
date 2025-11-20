package com.khanh.commerce.dto.response;

public record ProductResponseDTO(
        Long id,
        String name,
        Float price,
        String thumbnail, // Tên file ảnh
        String description,
        String categoryName // Trả về tên danh mục cho tiện
) {
}