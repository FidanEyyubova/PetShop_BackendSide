package com.petshop.backend.service.product;

import com.petshop.backend.dto.request.product.CreateProductRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.security.UserPrincipal;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {
    PageResponse<ProductResponse> getAllProducts(String search, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Pageable pageable);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(CreateProductRequest request, UserPrincipal currentUser);
    ProductResponse updateProduct(Long id, CreateProductRequest request, UserPrincipal currentUser);
    void deleteProduct(Long id, UserPrincipal currentUser);
}