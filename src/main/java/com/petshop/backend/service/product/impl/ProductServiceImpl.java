package com.petshop.backend.service.product.impl;

import com.petshop.backend.common.PetType;
import com.petshop.backend.common.ProductSort;
import com.petshop.backend.dto.request.product.CreateProductRequest;
import com.petshop.backend.dto.request.product.UpdateProductStatusRequest;
import com.petshop.backend.dto.request.product.UpdateProductStockRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.ProductCardResponse;
import com.petshop.backend.dto.response.product.ProductFacetsResponse;
import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.entity.product.Category;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.entity.product.SubCategory;
import com.petshop.backend.exception.AccessDeniedCustomException;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.product.CategoryRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.repository.product.SubCategoryRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.product.ProductFilter;
import com.petshop.backend.service.product.ProductMapper;
import com.petshop.backend.service.product.ProductService;
import com.petshop.backend.service.product.ProductSpecifications;
import com.petshop.backend.common.UserStatus;
import com.petshop.backend.util.JsonUtils;
import com.petshop.backend.util.PageUtils;
import com.petshop.backend.util.SlugUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final StoreRepository storeRepository;
    private final ProductMapper mapper;
    private final EntityManager entityManager;
    private final SecureRandom random = new SecureRandom();

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              SubCategoryRepository subCategoryRepository,
                              StoreRepository storeRepository,
                              ProductMapper mapper,
                              EntityManager entityManager) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.storeRepository = storeRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    // ---- query building --------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ProductFilter toFilter(SearchParams p, boolean includeHidden, Long vendorId, Boolean active) {
        if (p.minPrice() != null && p.minPrice().signum() < 0) {
            throw new BadRequestException("INVALID_PRICE_RANGE", "minPrice cannot be negative");
        }
        if (p.minPrice() != null && p.maxPrice() != null && p.minPrice().compareTo(p.maxPrice()) > 0) {
            throw new BadRequestException("INVALID_PRICE_RANGE", "minPrice cannot be greater than maxPrice");
        }
        Collection<Long> categoryIds = null;
        if (p.categoryId() != null) {
            categoryIds = List.of(p.categoryId());
        } else if (p.category() != null && !p.category().isBlank()) {
            String c = p.category().trim();
            if (c.matches("\\d+")) {
                categoryIds = List.of(Long.parseLong(c));
            } else {
                // Unknown slug -> empty result rather than 404.
                categoryIds = categoryRepository.findBySlugIgnoreCase(c).map(cat -> List.of(cat.getId())).orElse(List.of());
            }
        }
        return new ProductFilter(
                p.q(), categoryIds, p.subCategoryId(), PetType.normalizeId(p.petType()), p.brand(), p.storeId(), vendorId,
                p.minPrice(), p.maxPrice(), p.inStock(), p.minRating(), active, includeHidden, ProductSort.parse(p.sort()));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductCardResponse> search(ProductFilter filter, int page, int size) {
        Page<Product> result = productRepository.findAll(ProductSpecifications.of(filter), PageUtils.of(page, size));
        return PageResponse.of(result, mapper.toCards(result.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductFacetsResponse facets(ProductFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        long total = productRepository.count(ProductSpecifications.of(filter));

        // Categories (ignoring the category filter)
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<Product> root = cq.from(Product.class);
        cq.multiselect(root.get("categoryId"), cb.count(root))
                .where(and(cb, ProductSpecifications.predicates(root, cb, filter, ProductFilter.Dimension.CATEGORY)))
                .groupBy(root.get("categoryId"));
        Map<Long, Long> categoryCounts = new HashMap<>();
        for (Tuple t : entityManager.createQuery(cq).getResultList()) {
            if (t.get(0) != null) categoryCounts.put((Long) t.get(0), (Long) t.get(1));
        }
        List<ProductFacetsResponse.CategoryFacet> categories = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(c -> c.getType() == null || "PRODUCT".equalsIgnoreCase(c.getType()))
                .map(c -> new ProductFacetsResponse.CategoryFacet(c.getId(), c.getSlug(), c.getName(),
                        categoryCounts.getOrDefault(c.getId(), 0L)))
                .toList();

        // Pet types (ignoring the pet type filter)
        cq = cb.createTupleQuery();
        root = cq.from(Product.class);
        cq.multiselect(root.get("petType"), cb.count(root))
                .where(and(cb, ProductSpecifications.predicates(root, cb, filter, ProductFilter.Dimension.PET_TYPE)))
                .groupBy(root.get("petType"));
        Map<String, Long> petCounts = new HashMap<>();
        for (Tuple t : entityManager.createQuery(cq).getResultList()) {
            if (t.get(0) != null) petCounts.put((String) t.get(0), (Long) t.get(1));
        }
        List<ProductFacetsResponse.PetTypeFacet> petTypes = Arrays.stream(PetType.values())
                .map(pt -> new ProductFacetsResponse.PetTypeFacet(pt.getId(), pt.getDisplayName(), pt.getAzName(),
                        petCounts.getOrDefault(pt.getId(), 0L)))
                .toList();

        // Brands (ignoring the brand filter)
        cq = cb.createTupleQuery();
        root = cq.from(Product.class);
        cq.multiselect(root.get("brand"), cb.count(root))
                .where(and(cb, ProductSpecifications.predicates(root, cb, filter, ProductFilter.Dimension.BRAND)))
                .groupBy(root.get("brand"))
                .orderBy(cb.desc(cb.count(root)));
        List<ProductFacetsResponse.BrandFacet> brands = entityManager.createQuery(cq).setMaxResults(30).getResultList()
                .stream().filter(t -> t.get(0) != null)
                .map(t -> new ProductFacetsResponse.BrandFacet((String) t.get(0), (Long) t.get(1)))
                .toList();

        // Price range (ignoring the price filter)
        CriteriaQuery<Tuple> pq = cb.createTupleQuery();
        Root<Product> pr = pq.from(Product.class);
        Expression<BigDecimal> effective = ProductSpecifications.effectivePrice(pr, cb);
        pq.multiselect(cb.min(effective), cb.max(effective))
                .where(and(cb, ProductSpecifications.predicates(pr, cb, filter, ProductFilter.Dimension.PRICE)));
        Tuple range = entityManager.createQuery(pq).getSingleResult();

        return ProductFacetsResponse.builder()
                .total(total)
                .categories(categories)
                .petTypes(petTypes)
                .brands(brands)
                .priceRange(new ProductFacetsResponse.PriceRange((BigDecimal) range.get(0), (BigDecimal) range.get(1)))
                .build();
    }

    private static Predicate and(CriteriaBuilder cb, List<Predicate> predicates) {
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    // ---- public reads -----------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getPublicProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .filter(Product::isPubliclyVisible)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapper.toDetail(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getPublicProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndDeletedAtIsNull(slug)
                .filter(Product::isPubliclyVisible)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        return mapper.toDetail(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardResponse> getBestSellers(int limit) {
        int safe = Math.min(Math.max(limit, 1), 24);
        List<Long> ids = productRepository.findBestSellerIds(PageRequest.of(0, safe));
        List<Product> products;
        if (ids.isEmpty()) {
            // New shop without sales yet: fall back to the best rated / newest products.
            products = productRepository.findAll(ProductSpecifications.of(new ProductFilter(null, null, null, null, null,
                    null, null, null, null, null, null, null, false, ProductSort.RATING)), PageRequest.of(0, safe)).getContent();
        } else {
            Map<Long, Product> byId = new HashMap<>();
            productRepository.findAllById(ids).forEach(p -> byId.put(p.getId(), p));
            products = ids.stream().map(byId::get).filter(Objects::nonNull).toList();
        }
        return mapper.toCards(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardResponse> getRelated(Long id, int limit) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        int safe = Math.min(Math.max(limit, 1), 20);
        List<Product> related = productRepository.findRelated(id,
                product.getCategoryId() == null ? -1L : product.getCategoryId(),
                product.getPetType() == null ? "" : product.getPetType(),
                PageRequest.of(0, safe));
        return mapper.toCards(related);
    }

    // ---- vendor / admin writes ------------------------------------------------------------------

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        Store store = resolveTargetStore(request.getStoreId(), currentUser);

        Product product = Product.builder()
                .vendorId(store.getOwnerId())
                .storeId(store.getId())
                .slug(SlugUtils.unique(request.getTitle(), productRepository::existsBySlug))
                .build();
        apply(product, request, store);

        boolean approved = currentUser.isAdmin() || currentUser.getStatus() == UserStatus.ACTIVE;
        // Products of sellers waiting for approval are saved hidden.
        product.setIsActive(approved && !Boolean.FALSE.equals(request.getIsActive()));

        String sku = request.getSku() == null || request.getSku().isBlank() ? generateSku(store.getId()) : request.getSku().trim();
        if (productRepository.existsByStoreIdAndSkuIgnoreCaseAndDeletedAtIsNull(store.getId(), sku)) {
            throw new ConflictException("DUPLICATE_SKU", "SKU '" + sku + "' already exists in this store");
        }
        product.setSku(sku);
        return mapper.toDetail(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, CreateProductRequest request, UserPrincipal currentUser) {
        Product product = findManaged(id, currentUser);
        Store store = storeRepository.findById(product.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", product.getStoreId()));
        if (currentUser.isAdmin() && request.getStoreId() != null && !request.getStoreId().equals(product.getStoreId())) {
            store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store", "id", request.getStoreId()));
            product.setStoreId(store.getId());
            product.setVendorId(store.getOwnerId());
        }
        apply(product, request, store);

        if (request.getIsActive() != null) {
            setActive(product, request.getIsActive(), currentUser);
        }
        if (request.getSku() != null && !request.getSku().isBlank()) {
            String sku = request.getSku().trim();
            if (productRepository.existsByStoreIdAndSkuIgnoreCaseAndDeletedAtIsNullAndIdNot(product.getStoreId(), sku, id)) {
                throw new ConflictException("DUPLICATE_SKU", "SKU '" + sku + "' already exists in this store");
            }
            product.setSku(sku);
        }
        return mapper.toDetail(productRepository.save(product));
    }

    /** Soft delete: the product disappears from the shop, carts and wishlists but stays on past orders. */
    @Override
    @Transactional
    public void deleteProduct(Long id, UserPrincipal currentUser) {
        Product product = findManaged(id, currentUser);
        product.setDeletedAt(LocalDateTime.now());
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getManagedProduct(Long id, UserPrincipal currentUser) {
        return mapper.toDetail(findManaged(id, currentUser));
    }

    @Override
    @Transactional
    public ProductResponse updateStatus(Long id, UpdateProductStatusRequest request, UserPrincipal currentUser) {
        Product product = findManaged(id, currentUser);
        setActive(product, "active".equalsIgnoreCase(request.getStatus()), currentUser);
        return mapper.toDetail(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateStock(Long id, UpdateProductStockRequest request, UserPrincipal currentUser) {
        Product product = findManaged(id, currentUser);
        product.setStockQuantity(request.getStockQuantity());
        return mapper.toDetail(productRepository.save(product));
    }

    // ---- helpers ------------------------------------------------------------------------------------

    private Product findManaged(Long id, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        CurrentUser.requireOwnerOrAdmin(currentUser, product.getVendorId(), "product");
        return product;
    }

    private void setActive(Product product, boolean active, UserPrincipal currentUser) {
        if (active && !currentUser.isAdmin() && currentUser.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedCustomException("ACCOUNT_PENDING_APPROVAL",
                    "Products can be published after your account is approved");
        }
        product.setIsActive(active);
    }

    /** Vendors write into their own store; admins must name the store. */
    private Store resolveTargetStore(Long requestedStoreId, UserPrincipal currentUser) {
        if (currentUser.isAdmin()) {
            if (requestedStoreId == null) {
                throw new BadRequestException("STORE_REQUIRED", "storeId is required when an admin creates a product");
            }
            return storeRepository.findById(requestedStoreId)
                    .orElseThrow(() -> new ResourceNotFoundException("Store", "id", requestedStoreId));
        }
        if (!currentUser.isVendor()) {
            throw new AccessDeniedCustomException("Only vendors can create products");
        }
        Store store = storeRepository.findFirstByOwnerIdOrderByIdAsc(currentUser.getId())
                .orElseThrow(() -> new BadRequestException("STORE_REQUIRED", "Create your store before adding products"));
        if (requestedStoreId != null && !requestedStoreId.equals(store.getId())) {
            throw new AccessDeniedCustomException("NOT_OWNER", "You can only add products to your own store");
        }
        return store;
    }

    private void apply(Product product, CreateProductRequest r, Store store) {
        if (r.getDiscountPrice() != null && r.getDiscountPrice().signum() > 0 && r.getDiscountPrice().compareTo(r.getPrice()) >= 0) {
            throw new BadRequestException("INVALID_DISCOUNT", "discountPrice must be lower than price");
        }
        Category category = resolveCategory(r.getCategoryId(), r.getCategorySlug());
        SubCategory subCategory = null;
        if (r.getSubCategoryId() != null) {
            subCategory = subCategoryRepository.findById(r.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", "id", r.getSubCategoryId()));
            if (!subCategory.getCategory().getId().equals(category.getId())) {
                throw new BadRequestException("INVALID_SUBCATEGORY", "Subcategory does not belong to the selected category");
            }
        }

        product.setTitle(r.getTitle().trim());
        product.setBrand(r.getBrand().trim());
        product.setPetType(PetType.normalizeId(r.getPetType()));
        product.setCategoryId(category.getId());
        product.setCategoryName(category.getName());
        product.setSubCategoryId(subCategory != null ? subCategory.getId() : null);
        product.setSubCategoryName(subCategory != null ? subCategory.getTitle() : null);
        product.setPrice(r.getPrice());
        product.setDiscountPrice(r.getDiscountPrice() != null && r.getDiscountPrice().signum() > 0 ? r.getDiscountPrice() : null);
        product.setStockQuantity(r.getStockQuantity());
        product.setImages(clean(r.getImages()));
        product.setBadges(clean(r.getBadges()));
        product.setSizes(clean(r.getSizes()));
        product.setFlavors(clean(r.getFlavors()));
        product.setKeyBenefits(clean(r.getKeyBenefits()));
        product.setGuarantees(clean(r.getGuarantees()));
        product.setDescription(r.getDescription());
        product.setIngredients(r.getIngredients() == null ? null : JsonUtils.write(r.getIngredients()));
        product.setNutritionInfo(r.getNutritionInfo());
        product.setUsageGuide(r.getUsageGuide() == null ? null : JsonUtils.write(r.getUsageGuide()));
    }

    private Category resolveCategory(Long categoryId, String categorySlug) {
        if (categoryId != null) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        }
        if (categorySlug != null && !categorySlug.isBlank()) {
            return categoryRepository.findBySlugIgnoreCase(categorySlug.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", categorySlug));
        }
        throw new BadRequestException("CATEGORY_REQUIRED", "categoryId or categorySlug is required");
    }

    private static List<String> clean(List<String> values) {
        if (values == null) return new ArrayList<>();
        return new ArrayList<>(values.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).distinct().toList());
    }

    private String generateSku(Long storeId) {
        for (int i = 0; i < 10; i++) {
            String sku = "SKU-" + (100000 + random.nextInt(900000));
            if (!productRepository.existsByStoreIdAndSkuIgnoreCaseAndDeletedAtIsNull(storeId, sku)) return sku;
        }
        return "SKU-" + System.currentTimeMillis();
    }
}
