package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.BlogSubCategoryRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.BlogSubCategoryResponse;
import com.petshop.backend.service.content.BlogSubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/blog-sub-categories")
@Tag(name = "Admin - Content", description = "Site content, blog, contact messages and review moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBlogSubCategoryController {

    private final BlogSubCategoryService blogSubCategoryService;

    public AdminBlogSubCategoryController(BlogSubCategoryService blogSubCategoryService) {
        this.blogSubCategoryService = blogSubCategoryService;
    }

    @PostMapping
    @Operation(summary = "Create a blog category", description = "The slug is generated from the title when not sent.")
    public ResponseEntity<ApiResponse<BlogSubCategoryResponse>> createBlogSubCategory(@Valid @RequestBody BlogSubCategoryRequest request) {
        BlogSubCategoryResponse response = blogSubCategoryService.createBlogSubCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Blog SubCategory created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List blog categories", description = "Blog categories with their post counts.")
    public ResponseEntity<ApiResponse<List<BlogSubCategoryResponse>>> getAllBlogSubCategories() {
        List<BlogSubCategoryResponse> response = blogSubCategoryService.getAllBlogSubCategories();
        return ResponseEntity.ok(ApiResponse.success("Blog SubCategories retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a blog category", description = "One blog category with its post count.")
    public ResponseEntity<ApiResponse<BlogSubCategoryResponse>> getBlogSubCategoryById(@PathVariable Long id) {
        BlogSubCategoryResponse response = blogSubCategoryService.getBlogSubCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Blog SubCategory retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a blog category", description = "Rename the category or change its slug.")
    public ResponseEntity<ApiResponse<BlogSubCategoryResponse>> updateBlogSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody BlogSubCategoryRequest request) {
        BlogSubCategoryResponse response = blogSubCategoryService.updateBlogSubCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Blog SubCategory updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a blog category", description = "Returns 409 CATEGORY_IN_USE while posts use it.")
    public ResponseEntity<ApiResponse<Void>> deleteBlogSubCategory(@PathVariable Long id) {
        blogSubCategoryService.deleteBlogSubCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Blog SubCategory deleted successfully", null));
    }
}