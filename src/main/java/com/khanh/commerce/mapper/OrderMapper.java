package com.khanh.commerce.mapper;

import com.khanh.commerce.dto.request.OrderRequestDTO;
import com.khanh.commerce.dto.response.OrderResponseDTO;
import com.khanh.commerce.entity.Order;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {OrderDetailMapper.class, CartItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalMoney", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(source = "cartItems", target = "orderDetails")
    Order toOrder(OrderRequestDTO requestDTO);

    @Mapping(source = "orderDetails", target = "orderDetails")
    OrderResponseDTO toOrderResponse(Order order);

    // --- BỔ SUNG QUAN TRỌNG ---
    // Sau khi map xong Order và list OrderDetail,
    // method này sẽ chạy để gán Order hiện tại vào từng OrderDetail con.
    @AfterMapping
    default void linkOrderDetails(@MappingTarget Order order) {
        if (order.getOrderDetails() != null) {
            order.getOrderDetails().forEach(item -> item.setOrder(order));
        }
    }
}