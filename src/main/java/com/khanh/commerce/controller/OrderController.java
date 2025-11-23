package com.khanh.commerce.controller;

import com.khanh.commerce.dto.request.OrderRequestDTO;
import com.khanh.commerce.dto.request.OrderUpdateRequestDTO;
import com.khanh.commerce.dto.response.ApiResponse;
import com.khanh.commerce.dto.response.OrderResponseDTO;
import com.khanh.commerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        return ApiResponse.<OrderResponseDTO>builder()
                .message("Order created successfully")
                .data(orderService.createOrder(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<OrderResponseDTO>> getAllOrders() {
        return ApiResponse.<List<OrderResponseDTO>>builder()
                .message("Get all orders success")
                .data(orderService.getAllOrders())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<OrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ApiResponse.<OrderResponseDTO>builder()
                .message("Update order status success")
                .data(orderService.updateOrderStatus(id, status))
                .build();
    }

    @PutMapping("/{id}/info")
    public ApiResponse<OrderResponseDTO> updateOrderInfo(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateRequestDTO request
    ) {
        return ApiResponse.<OrderResponseDTO>builder()
                .message("Update order info success")
                .data(orderService.updateOrderInfo(id, request))
                .build();
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<?> cancelOrder(@PathVariable Long id) {
        return ApiResponse.builder()
                .message(orderService.cancelOrder(id))
                .build();
    }
}