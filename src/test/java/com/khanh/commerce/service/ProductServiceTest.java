package com.khanh.commerce.service;

import com.khanh.commerce.dto.request.ProductRequestDTO;
import com.khanh.commerce.dto.response.ProductResponseDTO;
import com.khanh.commerce.entity.Category;
import com.khanh.commerce.entity.Product;
import com.khanh.commerce.exception.ResourceNotFoundException;
import com.khanh.commerce.mapper.ProductMapper;
import com.khanh.commerce.repository.CategoryRepository;
import com.khanh.commerce.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private ProductMapper productMapper;
    
    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Create Product Success: Should save product and return DTO")
    void createProduct_Success() {
        // --- A. GIVEN (Chuẩn bị dữ liệu giả) ---
        Long categoryId = 1L;
        ProductRequestDTO request = new ProductRequestDTO("iPhone 15", 1000f, "Xịn", categoryId);
        MultipartFile mockFile = mock(MultipartFile.class); // Giả lập file

        Category mockCategory = new Category();
        mockCategory.setId(categoryId);
        mockCategory.setName("Phone");

        Product productBeforeSave = new Product(); // Kết quả của mapper.toProduct
        productBeforeSave.setName("iPhone 15");

        Product productAfterSave = new Product(); // Kết quả của repo.save
        productAfterSave.setId(100L);
        productAfterSave.setName("iPhone 15");
        productAfterSave.setThumbnail("uuid-img.jpg");
        productAfterSave.setCategory(mockCategory);

        ProductResponseDTO expectedResponse = new ProductResponseDTO(100L, "iPhone 15", 1000f, "uuid-img.jpg", "Xịn", "Phone");

        // --- B. WHEN (Dạy kịch bản cho Mocks) ---

        // 1. Khi tìm Category -> Trả về Category giả
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));

        // 2. Khi lưu file -> Trả về tên file giả
        when(fileStorageService.storeFile(mockFile)).thenReturn("uuid-img.jpg");

        // 3. Khi Map DTO -> Entity -> Trả về Entity giả
        when(productMapper.toProduct(request)).thenReturn(productBeforeSave);

        // 4. Khi Lưu Entity vào DB -> Trả về Entity đã có ID
        when(productRepository.save(any(Product.class))).thenReturn(productAfterSave);

        // 5. Khi Map Entity -> Response DTO -> Trả về DTO giả
        when(productMapper.toProductResponse(productAfterSave)).thenReturn(expectedResponse);

        // --- C. ACT (Chạy hàm thật) ---
        ProductResponseDTO result = productService.createProduct(request, mockFile);

        // --- D. ASSERT (Kiểm tra kết quả) ---
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals("iPhone 15", result.name());
        assertEquals("Phone", result.categoryName());

        // Kiểm tra xem hàm save() có thực sự được gọi 1 lần không
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Create Product Failed: Should throw Exception when Category not found")
    void createProduct_CategoryNotFound() {
        // --- A. GIVEN ---
        Long invalidCategoryId = 99L;
        ProductRequestDTO request = new ProductRequestDTO("iPhone", 1000f, "Desc", invalidCategoryId);
        MultipartFile mockFile = mock(MultipartFile.class);

        // Kịch bản: Tìm Category -> Trả về Rỗng (Empty)
        when(categoryRepository.findById(invalidCategoryId)).thenReturn(Optional.empty());

        // --- B. ACT & ASSERT (Chạy và mong chờ Lỗi) ---

        // Mong đợi: Ném ra ResourceNotFoundException
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(request, mockFile);
        });

        // Kiểm tra tin nhắn lỗi (phải khớp với chuỗi bạn viết trong Service)
        assertTrue(exception.getMessage().contains("Category not found"));

        // Đảm bảo KHÔNG BAO GIỜ lưu file hay lưu DB nếu Category lỗi
        verify(fileStorageService, never()).storeFile(any());
        verify(productRepository, never()).save(any());
    }
}