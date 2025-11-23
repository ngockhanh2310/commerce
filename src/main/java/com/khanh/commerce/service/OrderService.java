package com.khanh.commerce.service;

import com.khanh.commerce.dto.request.OrderRequestDTO;
import com.khanh.commerce.dto.request.OrderUpdateRequestDTO;
import com.khanh.commerce.dto.response.OrderDetailResponseDTO;
import com.khanh.commerce.dto.response.OrderResponseDTO;
import com.khanh.commerce.entity.Order;
import com.khanh.commerce.entity.OrderDetail;
import com.khanh.commerce.entity.Product;
import com.khanh.commerce.entity.User;
import com.khanh.commerce.exception.CustomAccessException;
import com.khanh.commerce.exception.DuplicateException;
import com.khanh.commerce.exception.ResourceNotFoundException;
import com.khanh.commerce.mapper.OrderMapper;
import com.khanh.commerce.model.OrderStatus;
import com.khanh.commerce.model.Role;
import com.khanh.commerce.repository.OrderRepository;
import com.khanh.commerce.repository.ProductRepository;
import com.khanh.commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderMapper.toOrder(request);

        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        float totalOrderMoney = 0;

        for (OrderDetail detail : order.getOrderDetails()) {
            Product realProduct = productRepository.findById(detail.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            detail.setProduct(realProduct);
            detail.setPrice(realProduct.getPrice());
            detail.setTotalMoney(realProduct.getPrice() * detail.getNumberOfProducts());
            totalOrderMoney += detail.getTotalMoney();
        }

        order.setTotalMoney(totalOrderMoney);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponse(savedOrder);
    }

    private OrderResponseDTO mapToOrderResponse(Order order) {
        List<OrderDetailResponseDTO> details = order.getOrderDetails().stream()
                .map(detail -> new OrderDetailResponseDTO(
                        detail.getProduct().getId(),
                        detail.getProduct().getName(),
                        detail.getPrice(),
                        detail.getNumberOfProducts(),
                        detail.getTotalMoney()
                ))
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getId(),
                order.getFullName(),
                order.getPhoneNumber(),
                order.getAddress(),
                order.getNote(),
                order.getEmail(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalMoney(),
                details
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Order> orders;
        if (user.getRole() == Role.ADMIN) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByUserId(user.getId());
        }

        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        return mapToOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO updateOrderInfo(Long orderId, OrderUpdateRequestDTO request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // GET CURRENT USER
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // CHECK ROLE
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new CustomAccessException("You do not have permission to update this order");
        }

        // UPDATE ORDER INFO IF STATUS IS PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new DuplicateException(List.of("Cannot update order information because it is " + order.getStatus()));
        }

        order.setFullName(request.fullName());
        order.setEmail(request.email());
        order.setPhoneNumber(request.phoneNumber());
        order.setAddress(request.address());
        order.setNote(request.note());

        return mapToOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public String cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentUser.getRole() != Role.ADMIN) {
            throw new CustomAccessException("You do not have permission to cancel this order");
        }
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new CustomAccessException("Only your order can be canceled");
        }

        // 4. CANCEL ORDER IF STATUS IS PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new DuplicateException(List.of("Cannot cancel order because it is " + order.getStatus()));
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return "Order cancelled successfully";
    }
}