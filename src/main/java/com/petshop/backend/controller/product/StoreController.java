package com.petshop.backend.controller.product;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.product.CreateStoreRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.ProductCardResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.product.ProductService;
import com.petshop.backend.service.product.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "Stores", description = "Public store pages and store products")
public class StoreController {

    private final StoreService storeService;
    private final ProductService productService;

    public StoreController(StoreService storeService, ProductService productService) {
        this.storeService = storeService;
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List stores", description = "Best rated first; optional name search and city filter.")
    public ResponseEntity<ApiResponse<PageResponse<StoreResponse>>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.success("Stores fetched", storeService.getAllStores(q, city, page, size)));
    }

    @GetMapping("/{idOrSlug}")
    @Operation(summary = "Get a store", description = "By numeric id or slug: profile, coordinates, hours, features, rating and product count.")
    public ResponseEntity<ApiResponse<StoreResponse>> get(@PathVariable String idOrSlug,
                                                          @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Store fetched", storeService.getStore(idOrSlug, currentUser)));
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "List a store's products", description = "Same filters and sorting as /products.")
    public ResponseEntity<ApiResponse<PageResponse<ProductCardResponse>>> products(
            @PathVariable Long id,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String petType,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        var params = new ProductService.SearchParams(q, category, null, null, petType, null, id, null, null, null, null, sort);
        return ResponseEntity.ok(ApiResponse.success("Store products fetched",
                productService.search(productService.toFilter(params, false, null, null), page, size)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
    @Operation(summary = "Create a store", description = "Vendors get a store at registration; a second store returns 409.")
    public ResponseEntity<ApiResponse<StoreResponse>> create(@Valid @RequestBody CreateStoreRequest request,
                                                             @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Store created", storeService.createStore(request, currentUser)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
    @Operation(summary = "Update a store", description = "Owner or admin only.")
    public ResponseEntity<ApiResponse<StoreResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody CreateStoreRequest request,
                                                             @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Store updated", storeService.updateStore(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
    @Operation(summary = "Delete a store", description = "Owner or admin only; 409 while the store has active products.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        storeService.deleteStore(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Store deleted"));
    }
}
