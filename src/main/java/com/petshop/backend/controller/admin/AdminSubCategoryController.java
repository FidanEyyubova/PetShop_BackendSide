package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.product.SubCategoryRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.product.SubCategoryResponse;
import com.petshop.backend.service.product.SubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/sub-categories")
@Tag(name = "Admin - Catalogue", description = "Categories, subcategories and products of every store")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubCategoryController {

    private final SubCategoryService subCategoryService;

    public AdminSubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @PostMapping
    @Operation(summary = "Create a subcategory", description = "categoryId must point to an existing category.")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> createSubCategory(@Valid @RequestBody SubCategoryRequest request) {
        SubCategoryResponse response = subCategoryService.createSubCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("SubCategory created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List subcategories", description = "All subcategories with their parent category.")
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> getAllSubCategories() {
        List<SubCategoryResponse> response = subCategoryService.getAllSubCategories();
        return ResponseEntity.ok(ApiResponse.success("SubCategories retrieved successfully", response));
    }

    @GetMapping("/by-category/{categoryId}")
    @Operation(summary = "List subcategories of a category", description = "Subcategories that belong to one category.")
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> getSubCategoriesByCategoryId(@PathVariable Long categoryId) {
        List<SubCategoryResponse> response = subCategoryService.getSubCategoriesByCategoryId(categoryId);
        return ResponseEntity.ok(ApiResponse.success("SubCategories retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a subcategory", description = "One subcategory.")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> getSubCategoryById(@PathVariable Long id) {
        SubCategoryResponse response = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("SubCategory retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a subcategory", description = "Can also move it to another category.")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> updateSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody SubCategoryRequest request) {
        SubCategoryResponse response = subCategoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("SubCategory updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a subcategory", description = "Removes the subcategory.")
    public ResponseEntity<ApiResponse<Void>> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.ok(ApiResponse.success("SubCategory deleted successfully", null));
    }
}