package com.khanh.commerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float price;

    @Column(name = "number_of_products")
    private int numberOfProducts;

    @Column(name = "total_money")
    private Float totalMoney;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // Quan hệ: Là sản phẩm nào?
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}