package com.petshop.backend.service.product.impl;

import com.petshop.backend.dto.request.product.CategoryRequest;
import com.petshop.backend.dto.response.product.CategoryResponse;
import com.petshop.backend.dto.response.product.SubCategoryResponse;
import com.petshop.backend.entity.product.Category;
import com.petshop.backend.entity.product.SubCategory;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.product.CategoryRepository;
import com.petshop.backend.service.product.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getTitle());
        category.setIcon(request.getIcon());
        category.setImageUrl(request.getIcon());
        category.setType(request.getType());
        category.setSlug(generateSlug(request.getTitle()));

        Category saved = categoryRepository.save(category);
        return mapToCategoryResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setName(request.getTitle());
        category.setIcon(request.getIcon());
        category.setImageUrl(request.getIcon());
        category.setType(request.getType());
        category.setSlug(generateSlug(request.getTitle()));

        Category updated = categoryRepository.save(category);
        return mapToCategoryResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.delete(category);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        List<SubCategoryResponse> subCategoryResponses = category.getSubCategories() != null ?
                category.getSubCategories().stream()
                        .map(this::mapToSubCategoryResponse)
                        .collect(Collectors.toList()) : Collections.emptyList();

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getSlug(),
                category.getType(),
                subCategoryResponses,
                category.getCreatedAt()
        );
    }

    private SubCategoryResponse mapToSubCategoryResponse(SubCategory subCategory) {
        return new SubCategoryResponse(
                subCategory.getId(),
                subCategory.getTitle(),
                subCategory.getIcon(),
                subCategory.getCategory().getId(),
                subCategory.getCategory().getName(),
                subCategory.getCreatedAt()
        );
    }

    private String generateSlug(String title) {
        if (title == null) return "";
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }
}