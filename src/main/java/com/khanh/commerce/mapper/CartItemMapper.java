package com.khanh.commerce.mapper;

import com.khanh.commerce.dto.request.CartItemDTO;
import com.khanh.commerce.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "quantity", target = "numberOfProducts")
    @Mapping(target = "totalMoney", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    OrderDetail toOrderDetail(CartItemDTO cartItemDTO);
}
