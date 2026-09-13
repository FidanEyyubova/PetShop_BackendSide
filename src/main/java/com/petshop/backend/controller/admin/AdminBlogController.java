package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.common.CreateBlogRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.BlogResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.service.content.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/blogs")
@Tag(name = "Admin Panel", description = "Unified Admin Panel management endpoints (Hero, Category, SubCategory, Product, Admin-Service, About, Blog, Contact, Messages, Settings)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBlogController {

    private final BlogService blogService;

    public AdminBlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping
    @Operation(summary = "Blog Section - Post new blog")
    public ResponseEntity<ApiResponse<BlogResponse>> createBlog(@Valid @RequestBody CreateBlogRequest request) {
        BlogResponse response = blogService.createBlog(request, null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Blog post created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Blog Section - Get all blogs")
    public ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<BlogResponse> response = blogService.getAllBlogs(pageable);
        return ResponseEntity.ok(ApiResponse.success("Blog posts retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Blog Section - Get blog by ID")
    public ResponseEntity<ApiResponse<BlogResponse>> getBlogById(@PathVariable Long id) {
        BlogResponse response = blogService.getBlogById(id);
        return ResponseEntity.ok(ApiResponse.success("Blog post details retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Blog Section - Update blog by ID")
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody CreateBlogRequest request) {
        BlogResponse response = blogService.updateBlog(id, request, null);
        return ResponseEntity.ok(ApiResponse.success("Blog post updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Blog Section - Delete blog by ID")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id, null);
        return ResponseEntity.ok(ApiResponse.success("Blog post deleted successfully", null));
    }
}