package com.petshop.backend.service.content.impl;

import com.petshop.backend.dto.request.common.CreateBlogRequest;
import com.petshop.backend.dto.response.common.BlogResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.entity.content.Blog;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.BlogRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.BlogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlogResponse> getAllBlogs(@NonNull Pageable pageable) {
        Page<Blog> blogPage = blogRepository.findAll(pageable);
        List<BlogResponse> content = blogPage.getContent().stream()
                .map(this::mapToBlogResponse)
                .toList();

        return PageResponse.<BlogResponse>builder()
                .content(content)
                .pageNumber(blogPage.getNumber())
                .pageSize(blogPage.getSize())
                .totalElements(blogPage.getTotalElements())
                .totalPages(blogPage.getTotalPages())
                .last(blogPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponse getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog mÉ™qalÉ™si tapÄ±lmadÄ±: " + id));
        return mapToBlogResponse(blog);
    }

    @Override
    @Transactional
    public BlogResponse createBlog(CreateBlogRequest request, UserPrincipal currentUser) {
        String slug = request.getTitle().toLowerCase().replaceAll("[^a-z0-9]", "-") + "-" + System.currentTimeMillis();

        Blog blog = Blog.builder()
                .title(request.getTitle())
                .slug(slug)
                .authorId(currentUser.getId())
                .excerpt(request.getExcerpt())
                .content(request.getContent())
                .coverImage(request.getCoverImage())
                .readTime(request.getReadTime())
                .tags(request.getTags())
                .build();

        Blog savedBlog = blogRepository.save(blog);
        return mapToBlogResponse(savedBlog);
    }

    private BlogResponse mapToBlogResponse(Blog blog) {
        return BlogResponse.builder()
                .id(blog.getId())
                .authorId(blog.getAuthorId())
                .title(blog.getTitle())
                .slug(blog.getSlug())
                .excerpt(blog.getExcerpt())
                .content(blog.getContent())
                .coverImage(blog.getCoverImage())
                .tags(blog.getTags())
                .readTime(blog.getReadTime())
                .createdAt(blog.getCreatedAt())
                .build();
    }
    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }
}