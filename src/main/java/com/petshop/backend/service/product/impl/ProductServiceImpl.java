package com.petshop.backend.service.product.impl;

import com.petshop.backend.dto.request.product.CreateProductRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.product.CategoryRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.repository.product.SubCategoryRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.product.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final StoreRepository storeRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              SubCategoryRepository subCategoryRepository,
                              StoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.storeRepository = storeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(
            String search,
            Long categoryId,
            Long subCategoryId,
            String petType,
            String brand,
            Long storeId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Pageable pageable) {

        Page<Product> productPage = productRepository.filterProducts(
                search, categoryId, subCategoryId, petType, null, brand, storeId, minPrice, maxPrice, inStock, pageable);
        List<ProductResponse> content = productPage.getContent().stream()
                .map(this::mapToProductResponse)
                .toList();

        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return mapToProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, UserPrincipal currentUser) {
        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }
        if (request.getSubCategoryId() != null) {
            subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", request.getSubCategoryId()));
        }
        if (request.getStoreId() != null) {
            storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store", "id", request.getStoreId()));
        }

        String slug = request.getTitle().toLowerCase().replaceAll("[^a-z0-9]", "-") + "-" + System.currentTimeMillis();

        Product product = Product.builder()
                .title(request.getTitle())
                .slug(slug)
                .sku(request.getSku())
                .brand(request.getBrand())
                .petType(request.getPetType())
                .categoryName(request.getCategoryName())
                .categoryId(request.getCategoryId())
                .subCategoryName(request.getSubCategoryName())
                .subCategoryId(request.getSubCategoryId())
                .storeId(request.getStoreId())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .stockQuantity(request.getStockQuantity())
                .badges(request.getBadges())
                .sizes(request.getSizes())
                .flavors(request.getFlavors())
                .images(request.getImages())
                .description(request.getDescription())
                .keyBenefits(request.getKeyBenefits())
                .guarantees(request.getGuarantees())
                .ingredients(request.getIngredients())
                .nutritionInfo(request.getNutritionInfo())
                .usageGuide(request.getUsageGuide())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, CreateProductRequest request, UserPrincipal currentUser) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }
        if (request.getSubCategoryId() != null) {
            subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", request.getSubCategoryId()));
        }
        if (request.getStoreId() != null) {
            storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store", "id", request.getStoreId()));
        }

        product.setTitle(request.getTitle());
        product.setSku(request.getSku());
        product.setBrand(request.getBrand());
        product.setPetType(request.getPetType());
        product.setCategoryId(request.getCategoryId());
        product.setCategoryName(request.getCategoryName());
        product.setSubCategoryId(request.getSubCategoryId());
        product.setSubCategoryName(request.getSubCategoryName());
        product.setStoreId(request.getStoreId());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setDescription(request.getDescription());
        product.setImages(request.getImages());
        product.setBadges(request.getBadges());
        product.setSizes(request.getSizes());
        product.setFlavors(request.getFlavors());
        product.setKeyBenefits(request.getKeyBenefits());
        product.setGuarantees(request.getGuarantees());
        product.setIngredients(request.getIngredients());
        product.setNutritionInfo(request.getNutritionInfo());
        product.setUsageGuide(request.getUsageGuide());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id, UserPrincipal currentUser) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        productRepository.delete(product);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .slug(product.getSlug())
                .sku(product.getSku())
                .brand(product.getBrand())
                .petType(product.getPetType())
                .categoryName(product.getCategoryName())
                .categoryId(product.getCategoryId())
                .subCategoryName(product.getSubCategoryName())
                .subCategoryId(product.getSubCategoryId())
                .storeId(product.getStoreId())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .stockQuantity(product.getStockQuantity())
                .badges(product.getBadges())
                .sizes(product.getSizes())
                .flavors(product.getFlavors())
                .images(product.getImages())
                .description(product.getDescription())
                .keyBenefits(product.getKeyBenefits())
                .guarantees(product.getGuarantees())
                .ingredients(product.getIngredients())
                .nutritionInfo(product.getNutritionInfo())
                .usageGuide(product.getUsageGuide())
                .rating(product.getRating())
                .build();
    }
}