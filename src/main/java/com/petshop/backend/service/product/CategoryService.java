package com.petshop.backend.service.product;

import com.petshop.backend.dto.response.product.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
}