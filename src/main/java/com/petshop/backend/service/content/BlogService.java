package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.common.CreateBlogRequest;
import com.petshop.backend.dto.response.common.BlogResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.security.UserPrincipal;

import java.util.List;

public interface BlogService {
    PageResponse<BlogResponse> getBlogs(String category, String q, int page, int size);
    BlogResponse getBlog(String idOrSlug);
    List<BlogResponse> getRelated(Long id, int limit);
    BlogResponse createBlog(CreateBlogRequest request, UserPrincipal currentUser);
    BlogResponse updateBlog(Long id, CreateBlogRequest request, UserPrincipal currentUser);
    void deleteBlog(Long id, UserPrincipal currentUser);
}
