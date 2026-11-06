package com.petshop.backend.service.content.impl;

import com.petshop.backend.i18n.Localized;
import com.petshop.backend.i18n.TranslatableFields;
import com.petshop.backend.dto.request.content.BlogSubCategoryRequest;
import com.petshop.backend.dto.response.content.BlogSubCategoryResponse;
import com.petshop.backend.entity.content.BlogSubCategory;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.BlogRepository;
import com.petshop.backend.repository.content.BlogSubCategoryRepository;
import com.petshop.backend.service.content.BlogSubCategoryService;
import com.petshop.backend.util.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlogSubCategoryServiceImpl implements BlogSubCategoryService {

    private final BlogSubCategoryRepository repository;
    private final BlogRepository blogRepository;

    public BlogSubCategoryServiceImpl(BlogSubCategoryRepository repository, BlogRepository blogRepository) {
        this.repository = repository;
        this.blogRepository = blogRepository;
    }

    @Override
    @Transactional
    public BlogSubCategoryResponse createBlogSubCategory(BlogSubCategoryRequest request) {
        BlogSubCategory category = new BlogSubCategory();
        apply(category, request);
        return toResponse(repository.save(category), 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogSubCategoryResponse> getAllBlogSubCategories() {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : blogRepository.countByCategory()) counts.put((Long) row[0], (Long) row[1]);
        return repository.findAllByOrderByTitleAsc().stream()
                .map(c -> toResponse(c, counts.getOrDefault(c.getId(), 0L))).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BlogSubCategoryResponse getBlogSubCategoryById(Long id) {
        BlogSubCategory c = find(id);
        return toResponse(c, blogRepository.countByCategoryId(id));
    }

    @Override
    @Transactional
    public BlogSubCategoryResponse updateBlogSubCategory(Long id, BlogSubCategoryRequest request) {
        BlogSubCategory c = find(id);
        apply(c, request);
        return toResponse(repository.save(c), blogRepository.countByCategoryId(id));
    }

    @Override
    @Transactional
    public void deleteBlogSubCategory(Long id) {
        BlogSubCategory c = find(id);
        if (blogRepository.countByCategoryId(id) > 0) {
            throw new ConflictException("CATEGORY_IN_USE", "Category still has posts; move them first");
        }
        repository.delete(c);
    }

    private BlogSubCategory find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Blog category", "id", id));
    }

    private void apply(BlogSubCategory c, BlogSubCategoryRequest r) {
        c.setTitle(r.getTitle().trim());
        c.setTranslations(c.getTranslations().mergedWith(r.getTranslations(), TranslatableFields.BLOG_CATEGORY));
        String wanted = r.getSlug() != null && !r.getSlug().isBlank() ? r.getSlug() : r.getTitle();
        if (c.getSlug() == null || r.getSlug() != null) {
            String current = c.getSlug();
            c.setSlug(SlugUtils.unique(wanted, s -> !s.equals(current) && repository.existsBySlug(s)));
        }
    }

    private static BlogSubCategoryResponse toResponse(BlogSubCategory c, long count) {
        return BlogSubCategoryResponse.builder()
                .id(c.getId()).title(categoryTitle(c)).slug(c.getSlug()).count(count).createdAt(c.getCreatedAt())
                .translations(Localized.editable(c.getTranslations(), "title", c.getTitle()))
                .build();
    }

    static String categoryTitle(BlogSubCategory c) {
        return Localized.of(c.getTranslations()).text("title", c.getTitle());
    }
}
