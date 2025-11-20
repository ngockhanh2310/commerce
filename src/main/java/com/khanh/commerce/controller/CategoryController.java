package com.khanh.commerce.controller;

import com.khanh.commerce.dto.request.CategoryRequestDTO;
import com.khanh.commerce.dto.response.ApiResponse;
import com.khanh.commerce.dto.response.CategoryResponseDTO;
import com.khanh.commerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    public ApiResponse<CategoryResponseDTO> createCategory(@RequestBody @Valid CategoryRequestDTO request) {
        return ApiResponse.<CategoryResponseDTO>builder()
                .message("Category created successfully")
                .data(categoryService.createCategory(request))
                .build();
    }

    @GetMapping("/list")
    public ApiResponse<List<CategoryResponseDTO>> getAllCategories() {
        return ApiResponse.<List<CategoryResponseDTO>>builder()
                .message("Get all categories success")
                .data(categoryService.getAllCategories())
                .build();
    }
}
