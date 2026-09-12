package com.petshop.backend.controller.content;

import com.petshop.backend.dto.request.common.CreateBlogRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.BlogResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.BlogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BlogResponse> response = blogService.getAllBlogs(pageable);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<BlogResponse>>builder()
                        .success(true)
                        .message("Bloglar siyahÄ±sÄ±")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogResponse>> getBlogById(@PathVariable Long id) {
        BlogResponse response = blogService.getBlogById(id);
        return ResponseEntity.ok(
                ApiResponse.<BlogResponse>builder()
                        .success(true)
                        .message("Blog detalÄ±")
                        .data(response)
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponse>> createBlog(
            @Valid @RequestBody CreateBlogRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        BlogResponse response = blogService.createBlog(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BlogResponse>builder()
                        .success(true)
                        .message("Blog uÄŸurla yaradÄ±ldÄ±")
                        .data(response)
                        .build());
    }
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }
}