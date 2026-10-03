package com.petshop.backend.service.product.impl;

import com.petshop.backend.common.PetType;
import com.petshop.backend.dto.request.product.CategoryRequest;
import com.petshop.backend.dto.response.product.CategoryResponse;
import com.petshop.backend.dto.response.product.PetTypeResponse;
import com.petshop.backend.dto.response.product.SubCategoryResponse;
import com.petshop.backend.entity.product.Category;
import com.petshop.backend.entity.product.SubCategory;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.product.CategoryRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.service.product.CategoryService;
import com.petshop.backend.util.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        apply(category, request, null);
        return toResponse(categoryRepository.save(category), Map.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        Map<Long, Long> counts = productCounts();
        return categoryRepository.findAllByOrderByNameAsc().stream().map(c -> toResponse(c, counts)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getPublicCategories(String type) {
        Map<Long, Long> counts = productCounts();
        List<Category> categories = type == null || type.isBlank()
                ? categoryRepository.findAllByOrderByNameAsc()
                : categoryRepository.findByTypeIgnoreCaseOrderByNameAsc(type.trim());
        return categories.stream().map(c -> toResponse(c, counts)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findWithSubCategoriesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return toResponse(category, productCounts());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByIdOrSlug(String idOrSlug) {
        if (idOrSlug.matches("\\d+")) return getCategoryById(Long.parseLong(idOrSlug));
        Category category = categoryRepository.findBySlugIgnoreCase(idOrSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", idOrSlug));
        return toResponse(category, productCounts());
    }

    @Override
    @Transactional

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        apply(category, request, id);
        return toResponse(categoryRepository.save(category), productCounts());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        if (productCounts().getOrDefault(id, 0L) > 0) {
            throw new ConflictException("CATEGORY_IN_USE", "Category still has products; move or delete them first");
        }
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetTypeResponse> getPetTypes() {
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : productRepository.countActiveByPetType()) {
            counts.put((String) row[0], (Long) row[1]);
        }
        return Arrays.stream(PetType.values())
                .map(pt -> PetTypeResponse.builder()
                        .id(pt.getId())
                        .name(pt.getDisplayName())
                        .azName(pt.getAzName())
                        .iconName(pt.getIconName())
                        .productCount(counts.getOrDefault(pt.getId(), 0L))
                        .build())
                .toList();
    }

    private void apply(Category category, CategoryRequest request, Long currentId) {
        category.setName(request.getTitle().trim());
        category.setIcon(request.getIcon());
        category.setImageUrl(request.getIcon());
        category.setType(request.getType() == null || request.getType().isBlank() ? "PRODUCT" : request.getType().trim().toUpperCase());
        String wanted = request.getSlug() != null && !request.getSlug().isBlank() ? request.getSlug() : request.getTitle();
        category.setSlug(SlugUtils.unique(wanted, slug -> categoryRepository.findBySlug(slug)
                .map(existing -> !existing.getId().equals(currentId)).orElse(false)));
    }

    private Map<Long, Long> productCounts() {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : productRepository.countActiveByCategory()) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        return counts;
    }

    private CategoryResponse toResponse(Category c, Map<Long, Long> counts) {
        List<SubCategoryResponse> subs = c.getSubCategories() == null ? List.of() : c.getSubCategories().stream()
                .sorted(Comparator.comparing(SubCategory::getTitle, String.CASE_INSENSITIVE_ORDER))
                .map(s -> new SubCategoryResponse(s.getId(), s.getTitle(), s.getIcon(), c.getId(), c.getName(), s.getCreatedAt()))
                .toList();
        return CategoryResponse.builder()
                .id(c.getId())
                .title(c.getName())
                .icon(c.getIcon())
                .slug(c.getSlug())
                .type(c.getType())
                .productCount(counts.getOrDefault(c.getId(), 0L))
                .subCategories(subs)
                .createdAt(c.getCreatedAt())
                .build();
    }
}
