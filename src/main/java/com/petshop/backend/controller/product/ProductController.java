package com.petshop.backend.controller.product;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.product.CreateProductRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.ProductCardResponse;
import com.petshop.backend.dto.response.product.ProductFacetsResponse;
import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Storefront catalogue: search, filters, facets, best sellers, related products, details")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Search products", description = "Active products with text search, category, pet type, brand, price and stock filters, sorting and paging.")
    public ResponseEntity<ApiResponse<PageResponse<ProductCardResponse>>> search(
            @Parameter(description = "Text search in title, brand, category, SKU") @RequestParam(required = false) String q,
            @Parameter(description = "Deprecated alias of q") @RequestParam(required = false) String search,
            @Parameter(description = "Category slug or id") @RequestParam(required = false) String category,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @Parameter(description = "dogs, cats, birds, fish, small-pets, reptiles") @RequestParam(required = false) String petType,
            @Parameter(description = "Alias of petType (frontend ?pet=)") @RequestParam(required = false) String pet,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "1-100") @RequestParam(defaultValue = "12") int size) {
        var params = new ProductService.SearchParams(q != null ? q : search, category, categoryId, subCategoryId,
                petType != null ? petType : pet, brand, storeId, minPrice, maxPrice, inStock, minRating, sort);
        var filter = productService.toFilter(params, false, null, null);
        return ResponseEntity.ok(ApiResponse.success("Products fetched", productService.search(filter, page, size)));
    }

    @GetMapping("/facets")
    @Operation(summary = "Get filter counts", description = "Counts next to each category, pet type and brand, plus the price range, for the current filters.")
    public ResponseEntity<ApiResponse<ProductFacetsResponse>> facets(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @RequestParam(required = false) String petType,
            @RequestParam(required = false) String pet,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock) {
        var params = new ProductService.SearchParams(q, category, categoryId, subCategoryId,
                petType != null ? petType : pet, brand, storeId, minPrice, maxPrice, inStock, null, null);
        var filter = productService.toFilter(params, false, null, null);
        return ResponseEntity.ok(ApiResponse.success("Facets fetched", productService.facets(filter)));
    }

    @GetMapping("/bestsellers")
    @Operation(summary = "List best sellers", description = "Most units sold; top-rated products when there are no sales yet.")
    public ResponseEntity<ApiResponse<List<ProductCardResponse>>> bestSellers(@RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Best sellers fetched", productService.getBestSellers(limit)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get a product by slug", description = "Same as GET /products/{id}, using the URL slug.")
    public ResponseEntity<ApiResponse<ProductResponse>> bySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched", productService.getPublicProductBySlug(slug)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product", description = "Full product page data. Hidden or deleted products return 404.")
    public ResponseEntity<ApiResponse<ProductResponse>> byId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched", productService.getPublicProduct(id)));
    }

    @GetMapping("/{id}/related")
    @Operation(summary = "List related products", description = "Same category first, then the same pet type.")
    public ResponseEntity<ApiResponse<List<ProductCardResponse>>> related(@PathVariable Long id,
                                                                          @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Related products fetched", productService.getRelated(id, limit)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
    @Operation(summary = "Create a product", 
            description = "Upload images via POST /api/v1/files first. Sellers waiting for approval create hidden products.")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody CreateProductRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created", productService.createProduct(request, currentUser)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
    @Operation(summary = "Update a product", description = "Replaces every field. Vendors can update only their own products.")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody CreateProductRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
    @Operation(summary = "Delete a product", description = "Soft delete: kept on past orders. Vendors can delete only their own products.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        productService.deleteProduct(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Product deleted"));
    }
}
