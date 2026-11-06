package com.petshop.backend.service.product.impl;

import com.petshop.backend.i18n.Localized;
import com.petshop.backend.i18n.TranslatableFields;
import com.petshop.backend.service.product.ProductMapper;
import com.petshop.backend.dto.request.product.SubCategoryRequest;
import com.petshop.backend.dto.response.product.SubCategoryResponse;
import com.petshop.backend.entity.product.Category;
import com.petshop.backend.entity.product.SubCategory;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.product.CategoryRepository;
import com.petshop.backend.repository.product.SubCategoryRepository;
import com.petshop.backend.service.product.SubCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository, CategoryRepository categoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public SubCategoryResponse createSubCategory(SubCategoryRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        SubCategory subCategory = new SubCategory();
        subCategory.setTitle(request.getTitle());
        subCategory.setTranslations(subCategory.getTranslations().mergedWith(request.getTranslations(), TranslatableFields.SUB_CATEGORY));
        subCategory.setIcon(request.getIcon());
        subCategory.setCategory(category);

        SubCategory saved = subCategoryRepository.save(subCategory);
        return mapToResponse(saved);
    }

    @Override
    public List<SubCategoryResponse> getAllSubCategories() {
        return subCategoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubCategoryResponse> getSubCategoriesByCategoryId(Long categoryId) {
        return subCategoryRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubCategoryResponse getSubCategoryById(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", id));
        return mapToResponse(subCategory);
    }

    @Override
    public SubCategoryResponse updateSubCategory(Long id, SubCategoryRequest request) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        subCategory.setTitle(request.getTitle());
        subCategory.setTranslations(subCategory.getTranslations().mergedWith(request.getTranslations(), TranslatableFields.SUB_CATEGORY));
        subCategory.setIcon(request.getIcon());
        subCategory.setCategory(category);

        SubCategory updated = subCategoryRepository.save(subCategory);
        return mapToResponse(updated);
    }

    @Override
    public void deleteSubCategory(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", id));
        subCategoryRepository.delete(subCategory);
    }

    private SubCategoryResponse mapToResponse(SubCategory subCategory) {
        return toResponse(subCategory, subCategory.getCategory());
    }

    static SubCategoryResponse toResponse(SubCategory s, Category category) {
        return SubCategoryResponse.builder()
                .id(s.getId())
                .title(ProductMapper.subCategoryName(s))
                .icon(s.getIcon())
                .categoryId(category.getId())
                .categoryName(ProductMapper.categoryName(category))
                .createdAt(s.getCreatedAt())
                .translations(Localized.editable(s.getTranslations(), "title", s.getTitle()))
                .build();
    }
}