package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.content.BlogSubCategoryRequest;
import com.petshop.backend.dto.response.content.BlogSubCategoryResponse;

import java.util.List;

public interface BlogSubCategoryService {
    BlogSubCategoryResponse createBlogSubCategory(BlogSubCategoryRequest request);
    List<BlogSubCategoryResponse> getAllBlogSubCategories();
    BlogSubCategoryResponse getBlogSubCategoryById(Long id);
    BlogSubCategoryResponse updateBlogSubCategory(Long id, BlogSubCategoryRequest request);
    void deleteBlogSubCategory(Long id);
}