package com.khanh.commerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    
    // "mappedBy" trỏ tới tên biến "category" trong class Product
    // CascadeType.ALL: Nếu xóa Category, xóa luôn tất cả Product thuộc về nó (Cẩn thận!)
    // FetchType.LAZY: Chỉ tải danh sách products khi cần dùng
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
}