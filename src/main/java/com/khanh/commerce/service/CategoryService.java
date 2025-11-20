package com.khanh.commerce.service;

import com.khanh.commerce.dto.request.CategoryRequestDTO;
import com.khanh.commerce.dto.response.CategoryResponseDTO;
import com.khanh.commerce.entity.Category;
import com.khanh.commerce.exception.DuplicateResourceException;
import com.khanh.commerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateResourceException("Category with name " + request.name() + " already exists");
        }
        Category category = categoryRepository.save(Category.builder()
                .name(request.name())
                .build());
        return new CategoryResponseDTO(category.getId(), category.getName());
    }

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(category -> new CategoryResponseDTO(
                        category.getId(),
                        category.getName()
                ))
                .toList();
    }
}
