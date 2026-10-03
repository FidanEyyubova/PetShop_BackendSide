package com.petshop.backend.service.product;

import com.petshop.backend.dto.request.product.CategoryRequest;
import com.petshop.backend.dto.response.product.CategoryResponse;
import com.petshop.backend.dto.response.product.PetTypeResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getPublicCategories(String type);
    CategoryResponse getCategoryById(Long id);
    CategoryResponse getCategoryByIdOrSlug(String idOrSlug);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    List<PetTypeResponse> getPetTypes();
}
