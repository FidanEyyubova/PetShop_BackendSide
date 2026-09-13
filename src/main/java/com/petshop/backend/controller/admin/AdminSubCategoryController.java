package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.product.SubCategoryRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.product.SubCategoryResponse;
import com.petshop.backend.service.product.SubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/sub-categories")
@Tag(name = "Admin Panel", description = "Unified Admin Panel management endpoints (Hero, Category, SubCategory, Product, Admin-Service, About, Blog, Contact, Messages, Settings)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubCategoryController {

    private final SubCategoryService subCategoryService;

    public AdminSubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @PostMapping
    @Operation(summary = "Sub-Category (Shop Page) - Add sub-category")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> createSubCategory(@Valid @RequestBody SubCategoryRequest request) {
        SubCategoryResponse response = subCategoryService.createSubCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("SubCategory created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Sub-Category (Shop Page) - Get all sub-categories")
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> getAllSubCategories() {
        List<SubCategoryResponse> response = subCategoryService.getAllSubCategories();
        return ResponseEntity.ok(ApiResponse.success("SubCategories retrieved successfully", response));
    }

    @GetMapping("/by-category/{categoryId}")
    @Operation(summary = "Sub-Category (Shop Page) - Get sub-categories by parent category ID")
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> getSubCategoriesByCategoryId(@PathVariable Long categoryId) {
        List<SubCategoryResponse> response = subCategoryService.getSubCategoriesByCategoryId(categoryId);
        return ResponseEntity.ok(ApiResponse.success("SubCategories retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Sub-Category (Shop Page) - Get sub-category by ID")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> getSubCategoryById(@PathVariable Long id) {
        SubCategoryResponse response = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("SubCategory retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Sub-Category (Shop Page) - Update sub-category by ID")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> updateSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody SubCategoryRequest request) {
        SubCategoryResponse response = subCategoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("SubCategory updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Sub-Category (Shop Page) - Delete sub-category by ID")
    public ResponseEntity<ApiResponse<Void>> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.ok(ApiResponse.success("SubCategory deleted successfully", null));
    }
}