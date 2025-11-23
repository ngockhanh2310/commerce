package com.khanh.commerce.mapper;

import com.khanh.commerce.dto.request.ProductRequestDTO;
import com.khanh.commerce.dto.response.ProductResponseDTO;
import com.khanh.commerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponseDTO toProductResponse(Product product);

    @Mapping(target = "category", ignore = true) // bỏ qua trường category khi map
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    Product toProduct(ProductRequestDTO requestDTO);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    void updateProduct(ProductRequestDTO requestDTO, @MappingTarget Product product);
}
