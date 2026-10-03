package com.petshop.backend.controller.admin;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.common.CreateBlogRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.BlogResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/blogs")
@PreAuthorize("hasRole('" + Roles.ADMIN + "')")
@Tag(name = "Admin - Content", description = "Site content, blog, contact messages and review moderation")
public class AdminBlogController {

    private final BlogService blogService;

    public AdminBlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping
    @Operation(summary = "Create a blog post", description = "The signed-in admin becomes the author; read time is computed when empty.")
    public ResponseEntity<ApiResponse<BlogResponse>> createBlog(@Valid @RequestBody CreateBlogRequest request,
                                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Blog post created", blogService.createBlog(request, currentUser)));
    }

    @GetMapping
    @Operation(summary = "List blog posts", description = "Newest first; filter by category and search text.")
    public ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> getAllBlogs(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Blog posts fetched", blogService.getBlogs(category, q, page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a blog post", description = "Full post including the content.")
    public ResponseEntity<ApiResponse<BlogResponse>> getBlogById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Blog post fetched", blogService.getBlog(String.valueOf(id))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a blog post", description = "Replaces the post's fields.")
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlog(@PathVariable Long id, @Valid @RequestBody CreateBlogRequest request,
                                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Blog post updated", blogService.updateBlog(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a blog post", description = "Removes the post.")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        blogService.deleteBlog(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Blog post deleted"));
    }
}
