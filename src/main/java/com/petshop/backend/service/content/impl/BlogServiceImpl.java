package com.petshop.backend.service.content.impl;

import com.petshop.backend.common.Language;
import com.petshop.backend.i18n.Localized;
import com.petshop.backend.i18n.TranslatableFields;
import com.petshop.backend.dto.request.common.CreateBlogRequest;
import com.petshop.backend.dto.response.common.BlogResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.entity.content.Blog;
import com.petshop.backend.entity.content.BlogSubCategory;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.BlogRepository;
import com.petshop.backend.repository.content.BlogSubCategoryRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.BlogService;
import com.petshop.backend.util.MaskUtils;
import com.petshop.backend.util.PageUtils;
import com.petshop.backend.util.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {

    private static final int WORDS_PER_MINUTE = 200;
    private static final java.util.regex.Pattern AUTO_READ_TIME = java.util.regex.Pattern.compile("^\\d+ (min read|dəq oxu|мин чтения)$");

    private final BlogRepository blogRepository;
    private final BlogSubCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public BlogServiceImpl(BlogRepository blogRepository, BlogSubCategoryRepository categoryRepository,
                           UserRepository userRepository) {
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlogResponse> getBlogs(String category, String q, int page, int size) {
        Long categoryId = null;
        if (category != null && !category.isBlank()) {
            Optional<BlogSubCategory> cat = category.matches("\\d+")
                    ? categoryRepository.findById(Long.parseLong(category))
                    : categoryRepository.findBySlugIgnoreCase(category.trim());
            if (cat.isEmpty()) {
                return new PageResponse<>(List.of(), Math.max(page, 0), size, 0, 0, true);
            }
            categoryId = cat.get().getId();
        }
        String pattern = q == null || q.isBlank() ? "%" : "%" + MaskUtils.escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%";
        Page<Blog> result = blogRepository.search(categoryId, pattern,
                PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id")));
        return PageResponse.of(result, toResponses(result.getContent(), false));
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponse getBlog(String idOrSlug) {
        Blog blog = (idOrSlug.matches("\\d+") ? blogRepository.findById(Long.parseLong(idOrSlug)) : blogRepository.findBySlug(idOrSlug))
                .orElseThrow(() -> new ResourceNotFoundException("Blog", "id", idOrSlug));
        return toResponses(List.of(blog), true).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponse> getRelated(Long id, int limit) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Blog", "id", id));
        int safe = Math.min(Math.max(limit, 1), 12);
        List<Blog> related = new ArrayList<>(blogRepository.findRelated(id, blog.getCategoryId(), PageRequest.of(0, safe)));
        if (related.size() < safe) {
            Set<Long> seen = related.stream().map(Blog::getId).collect(Collectors.toSet());
            seen.add(id);
            blogRepository.findRelated(id, null, PageRequest.of(0, safe + seen.size())).stream()
                    .filter(b -> seen.add(b.getId())).limit(safe - related.size()).forEach(related::add);
        }
        return toResponses(related, false);
    }

    @Override
    @Transactional
    public BlogResponse createBlog(CreateBlogRequest request, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        Blog blog = Blog.builder()
                .authorId(currentUser.getId())
                .slug(SlugUtils.unique(request.getTitle(), blogRepository::existsBySlug))
                .build();
        apply(blog, request);
        return toResponses(List.of(blogRepository.save(blog)), true).get(0);
    }

    @Override
    @Transactional
    public BlogResponse updateBlog(Long id, CreateBlogRequest request, UserPrincipal currentUser) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Blog", "id", id));
        apply(blog, request);
        return toResponses(List.of(blogRepository.save(blog)), true).get(0);
    }

    @Override
    @Transactional
    public void deleteBlog(Long id, UserPrincipal currentUser) {
        blogRepository.delete(blogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Blog", "id", id)));
    }

    private void apply(Blog blog, CreateBlogRequest r) {
        Long categoryId = r.getCategoryId();
        if (categoryId == null && r.getCategorySlug() != null && !r.getCategorySlug().isBlank()) {
            categoryId = categoryRepository.findBySlugIgnoreCase(r.getCategorySlug().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Blog category", "slug", r.getCategorySlug())).getId();
        } else if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Blog category", "id", categoryId);
        }
        blog.setTitle(r.getTitle().trim());
        blog.setExcerpt(r.getExcerpt());
        blog.setContent(r.getContent());
        blog.setCoverImage(r.getCoverImage());
        blog.setTags(r.getTags() == null ? new ArrayList<>()
                : new ArrayList<>(r.getTags().stream().filter(Objects::nonNull).map(String::trim).filter(t -> !t.isEmpty()).distinct().toList()));
        blog.setReadTime(r.getReadTime() != null && !r.getReadTime().isBlank() ? r.getReadTime().trim() : null);
        blog.setCategoryId(categoryId);
        blog.setTranslations(blog.getTranslations().mergedWith(r.getTranslations(), TranslatableFields.BLOG));
    }

    static String readTime(String content) {
        return readTime(content, Language.current());
    }

    static String readTime(String content, Language language) {
        int words = content == null || content.isBlank() ? 0 : content.trim().split("\\s+").length;
        int minutes = Math.max(1, (int) Math.ceil(words / (double) WORDS_PER_MINUTE));
        return switch (language) {
            case AZ -> minutes + " dəq oxu";
            case RU -> minutes + " мин чтения";
            case EN -> minutes + " min read";
        };
    }

    private static String localizedReadTime(Blog b, Localized t, String content) {
        String stored = t.text("readTime", b.getReadTime());
        if (stored == null || AUTO_READ_TIME.matcher(stored).matches()) return readTime(content);
        return stored;
    }

    private List<BlogResponse> toResponses(List<Blog> blogs, boolean withContent) {
        if (blogs.isEmpty()) return List.of();
        Map<Long, BlogSubCategory> categories = categoryRepository.findAllById(blogs.stream().map(Blog::getCategoryId)
                        .filter(Objects::nonNull).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(BlogSubCategory::getId, Function.identity()));
        Map<Long, User> authors = userRepository.findByIdIn(blogs.stream().map(Blog::getAuthorId)
                        .filter(Objects::nonNull).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(User::getId, Function.identity()));
        return blogs.stream().map(b -> {
            BlogSubCategory c = b.getCategoryId() == null ? null : categories.get(b.getCategoryId());
            User a = b.getAuthorId() == null ? null : authors.get(b.getAuthorId());
            Localized t = Localized.of(b.getTranslations());
            String content = t.text("content", b.getContent());
            return BlogResponse.builder()
                    .id(b.getId())
                    .title(t.text("title", b.getTitle()))
                    .slug(b.getSlug())
                    .excerpt(t.text("excerpt", b.getExcerpt()))
                    .content(withContent ? content : null)
                    .coverImage(b.getCoverImage())
                    .tags(t.list("tags", b.getTags()))
                    .readTime(localizedReadTime(b, t, content))
                    .categoryId(b.getCategoryId())
                    .categoryName(c != null ? BlogSubCategoryServiceImpl.categoryTitle(c) : null)
                    .categorySlug(c != null ? c.getSlug() : null)
                    .authorId(b.getAuthorId())
                    .authorName(a != null ? a.getFullName() : "PetShop")
                    .createdAt(b.getCreatedAt())
                    .updatedAt(b.getUpdatedAt())
                    .translations(withContent ? Localized.editable(b.getTranslations(), "title", b.getTitle(),
                            "excerpt", b.getExcerpt(), "content", b.getContent(), "tags", b.getTags(),
                            "readTime", b.getReadTime()) : null)
                    .build();
        }).toList();
    }
}
