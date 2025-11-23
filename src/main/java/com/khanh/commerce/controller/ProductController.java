package com.khanh.commerce.controller;

import com.khanh.commerce.dto.request.ProductRequestDTO;
import com.khanh.commerce.dto.response.ApiResponse;
import com.khanh.commerce.dto.response.ProductResponseDTO;
import com.khanh.commerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDTO> createProduct(
            @Valid @RequestPart("product") ProductRequestDTO request, // Phần JSON
            @RequestPart("image") MultipartFile image             // Phần File
    ) {
        return ApiResponse.<ProductResponseDTO>builder()
                .message("Product created successfully")
                .data(productService.createProduct(request, image))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<ProductResponseDTO>> getAllProducts(Pageable pageable) {
        return ApiResponse.<Page<ProductResponseDTO>>builder()
                .message("Get products list success")
                .data(productService.getAllProducts(pageable))
                .build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestPart("product") ProductRequestDTO request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return ApiResponse.<ProductResponseDTO>builder()
                .message("Product updated successfully")
                .data(productService.updateProduct(id, request, imageFile))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .message("Product deleted successfully")
                .build();
    }
}