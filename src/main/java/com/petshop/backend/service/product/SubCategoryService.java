package com.petshop.backend.service.product;

import com.petshop.backend.dto.request.product.SubCategoryRequest;
import com.petshop.backend.dto.response.product.SubCategoryResponse;

import java.util.List;

public interface SubCategoryService {
    SubCategoryResponse createSubCategory(SubCategoryRequest request);
    List<SubCategoryResponse> getAllSubCategories();
    List<SubCategoryResponse> getSubCategoriesByCategoryId(Long categoryId);
    SubCategoryResponse getSubCategoryById(Long id);
    SubCategoryResponse updateSubCategory(Long id, SubCategoryRequest request);
    void deleteSubCategory(Long id);
}