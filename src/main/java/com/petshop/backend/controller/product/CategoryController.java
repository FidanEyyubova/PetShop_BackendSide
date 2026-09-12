package com.petshop.backend.controller.product;

import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.product.CategoryResponse;
import com.petshop.backend.service.product.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder()
                        .success(true)
                        .message("Kateqoriyalar uÄŸurla gÉ™tirildi")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .success(true)
                        .message("Kateqoriya detalÄ±")
                        .data(response)
                        .build()
        );
    }
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
}