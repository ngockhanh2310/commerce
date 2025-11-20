package com.khanh.commerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Float price;

    private String thumbnail; // Đường dẫn ảnh (URL)

    @Column(columnDefinition = "TEXT") // Cho phép lưu văn bản dài
    private String description;

    // Foreign key
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}