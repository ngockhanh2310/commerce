package com.khanh.commerce.mapper;

import com.khanh.commerce.dto.request.CategoryRequestDTO;
import com.khanh.commerce.dto.response.CategoryResponseDTO;
import com.khanh.commerce.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDTO toCategoryResponse(Category category);

    @Mapping(target = "id", ignore = true)
    Category toCategory(CategoryRequestDTO requestDTO);
}
