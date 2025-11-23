package com.khanh.commerce.mapper;

import com.khanh.commerce.dto.response.OrderDetailResponseDTO;
import com.khanh.commerce.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "quantity", source = "numberOfProducts")
    OrderDetailResponseDTO toOrderDetailResponse(OrderDetail orderDetail);
}
