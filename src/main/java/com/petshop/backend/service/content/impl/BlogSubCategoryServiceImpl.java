package com.petshop.backend.service.content.impl;

import com.petshop.backend.dto.request.content.BlogSubCategoryRequest;
import com.petshop.backend.dto.response.content.BlogSubCategoryResponse;
import com.petshop.backend.entity.content.BlogSubCategory;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.BlogSubCategoryRepository;
import com.petshop.backend.service.content.BlogSubCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogSubCategoryServiceImpl implements BlogSubCategoryService {

    private final BlogSubCategoryRepository repository;

    public BlogSubCategoryServiceImpl(BlogSubCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public BlogSubCategoryResponse createBlogSubCategory(BlogSubCategoryRequest request) {
        BlogSubCategory subCategory = new BlogSubCategory();
        subCategory.setTitle(request.getTitle());

        BlogSubCategory saved = repository.save(subCategory);
        return mapToResponse(saved);
    }

    @Override
    public List<BlogSubCategoryResponse> getAllBlogSubCategories() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BlogSubCategoryResponse getBlogSubCategoryById(Long id) {
        BlogSubCategory subCategory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogSubCategory", "id", id));
        return mapToResponse(subCategory);
    }

    @Override
    public BlogSubCategoryResponse updateBlogSubCategory(Long id, BlogSubCategoryRequest request) {
        BlogSubCategory subCategory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogSubCategory", "id", id));

        subCategory.setTitle(request.getTitle());

        BlogSubCategory updated = repository.save(subCategory);
        return mapToResponse(updated);
    }

    @Override
    public void deleteBlogSubCategory(Long id) {
        BlogSubCategory subCategory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogSubCategory", "id", id));
        repository.delete(subCategory);
    }

    private BlogSubCategoryResponse mapToResponse(BlogSubCategory subCategory) {
        return new BlogSubCategoryResponse(
                subCategory.getId(),
                subCategory.getTitle(),
                subCategory.getCreatedAt()
        );
    }
}