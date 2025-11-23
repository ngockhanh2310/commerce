package com.khanh.commerce.service;

import com.khanh.commerce.dto.request.ProductRequestDTO;
import com.khanh.commerce.dto.response.ProductResponseDTO;
import com.khanh.commerce.entity.Category;
import com.khanh.commerce.entity.Product;
import com.khanh.commerce.exception.ResourceNotFoundException;
import com.khanh.commerce.mapper.ProductMapper;
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
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO request, MultipartFile imageFile) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.categoryId()));

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Product image is required");
        }

        // 2. Lưu file ảnh -> lấy tên file
        String thumbnailFileName = fileStorageService.storeFile(imageFile);

        Product product = productMapper.toProduct(request);
        product.setThumbnail(thumbnailFileName);
        product.setCategory(category);

        return productMapper.toProductResponse(productRepository.save(product));
    }

    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toProductResponse);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request, MultipartFile imageFile) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.categoryId()));

        productMapper.updateProduct(request, product);

        product.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            String newThumbnail = fileStorageService.storeFile(imageFile);
            product.setThumbnail(newThumbnail);
        }
        Product savedProduct = productRepository.save(product);

        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
        // delete image
        // fileStorageService.deleteFile(product.getThumbnail());
    }
}