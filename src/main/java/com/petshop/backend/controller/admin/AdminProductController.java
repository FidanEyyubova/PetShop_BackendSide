package com.petshop.backend.controller.admin;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.product.CreateProductRequest;
import com.petshop.backend.dto.request.product.UpdateProductStatusRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.ProductCardResponse;
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

@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasRole('" + Roles.ADMIN + "')")
@Tag(name = "Admin - Catalogue", description = "Categories, subcategories and products of every store")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List all products", description = "Every store's products, including hidden ones. Filter by text, category, pet type, store and status.")
    public ResponseEntity<ApiResponse<PageResponse<ProductCardResponse>>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String petType,
            @RequestParam(required = false) Long storeId,
            @Parameter(description = "active | hidden") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Boolean active = status == null || status.isBlank() ? null : "active".equalsIgnoreCase(status);
        var params = new ProductService.SearchParams(q, category, null, null, petType, null, storeId, null, null, null, null, sort);
        return ResponseEntity.ok(ApiResponse.success("Products fetched",
                productService.search(productService.toFilter(params, true, null, active), page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product", description = "Full product details, also for hidden products.")
    public ResponseEntity<ApiResponse<ProductResponse>> get(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched", productService.getManagedProduct(id, currentUser)));
    }

    @PostMapping
    @Operation(summary = "Create a product", description = "Admins create products for any store; storeId is required.")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody CreateProductRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created", productService.createProduct(request, currentUser)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Replaces all fields of any product.")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id, @Valid @RequestBody CreateProductRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, request, currentUser)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Show or hide a product", description = "status: active | hidden.")
    public ResponseEntity<ApiResponse<ProductResponse>> status(@PathVariable Long id, @Valid @RequestBody UpdateProductStatusRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product status updated", productService.updateStatus(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Soft delete: the product disappears from the shop but stays on past orders.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        productService.deleteProduct(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Product deleted"));
    }
}
