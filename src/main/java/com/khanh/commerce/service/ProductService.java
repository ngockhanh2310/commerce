package com.khanh.commerce.service;

import com.khanh.commerce.dto.request.ProductRequestDTO;
import com.khanh.commerce.dto.response.ProductResponseDTO;
import com.khanh.commerce.entity.Category;
import com.khanh.commerce.entity.Product;
import com.khanh.commerce.exception.ResourceNotFoundException;
import com.khanh.commerce.repository.CategoryRepository;
import com.khanh.commerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO request, MultipartFile imageFile) {
        // 1. Kiểm tra Category
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.categoryId()));

        // 2. Lưu file ảnh -> lấy tên file
        String thumbnailFileName = fileStorageService.storeFile(imageFile);

        // 3. Tạo Product Entity và lưu vào DB
        Product product = productRepository.save(Product.builder()
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .thumbnail(thumbnailFileName) // Lưu tên file vào DB
                .category(category)
                .build());

        // 5. Trả về DTO
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getThumbnail(),
                product.getDescription(),
                product.getCategory().getName()
        );
    }

    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        // 1. Gọi Repository lấy Page<Product>
        Page<Product> productPage = productRepository.findAll(pageable);

        // 2. Chuyển đổi từng Product -> ProductResponseDTO
        return productPage.map(product -> new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getThumbnail(),
                product.getDescription(),
                product.getCategory().getName()
        ));
    }
}