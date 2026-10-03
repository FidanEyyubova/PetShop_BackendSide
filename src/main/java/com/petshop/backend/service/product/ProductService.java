package com.petshop.backend.service.product;

import com.petshop.backend.dto.request.product.CreateProductRequest;
import com.petshop.backend.dto.request.product.UpdateProductStatusRequest;
import com.petshop.backend.dto.request.product.UpdateProductStockRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.ProductCardResponse;
import com.petshop.backend.dto.response.product.ProductFacetsResponse;
import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.security.UserPrincipal;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    /** Raw query parameters as received by the API; normalised by {@link #toFilter}. */
    record SearchParams(String q, String category, Long categoryId, Long subCategoryId, String petType, String brand,
                        Long storeId, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, BigDecimal minRating,
                        String sort) {}

    ProductFilter toFilter(SearchParams params, boolean includeHidden, Long vendorId, Boolean active);

    PageResponse<ProductCardResponse> search(ProductFilter filter, int page, int size);

    ProductFacetsResponse facets(ProductFilter filter);

    ProductResponse getPublicProduct(Long id);

    ProductResponse getPublicProductBySlug(String slug);

    List<ProductCardResponse> getBestSellers(int limit);

    List<ProductCardResponse> getRelated(Long id, int limit);

    ProductResponse createProduct(CreateProductRequest request, UserPrincipal currentUser);

    ProductResponse updateProduct(Long id, CreateProductRequest request, UserPrincipal currentUser);

    void deleteProduct(Long id, UserPrincipal currentUser);

    ProductResponse getManagedProduct(Long id, UserPrincipal currentUser);

    ProductResponse updateStatus(Long id, UpdateProductStatusRequest request, UserPrincipal currentUser);

    ProductResponse updateStock(Long id, UpdateProductStockRequest request, UserPrincipal currentUser);
}
