package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.common.CreateBlogRequest;
import com.petshop.backend.dto.response.common.BlogResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.security.UserPrincipal;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    PageResponse<BlogResponse> getAllBlogs(@NonNull Pageable pageable);
    BlogResponse getBlogById(Long id);
    BlogResponse createBlog(CreateBlogRequest request, UserPrincipal currentUser);
}