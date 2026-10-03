package com.petshop.backend.controller.vendor;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.product.CreateProductRequest;
import com.petshop.backend.dto.request.product.CreateStoreRequest;
import com.petshop.backend.dto.request.product.UpdateProductStatusRequest;
import com.petshop.backend.dto.request.product.UpdateProductStockRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.ProductCardResponse;
import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.product.ProductService;
import com.petshop.backend.service.product.StoreService;
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
@RequestMapping("/api/v1/vendor")
@PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
@Tag(name = "Vendor - Store & Products", description = "Vendor dashboard: own store profile and product management")
public class VendorCatalogController {

    private final StoreService storeService;
    private final ProductService productService;

    public VendorCatalogController(StoreService storeService, ProductService productService) {
        this.storeService = storeService;
        this.productService = productService;
    }

    @GetMapping("/stores/me")
    @Operation(summary = "Get my store", description = "My store profile including VÖEN.")
    public ResponseEntity<ApiResponse<StoreResponse>> myStore(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Store fetched", storeService.getMyStore(currentUser)));
    }

    @PutMapping("/stores/me")
    @Operation(summary = "Update my store", description = "Logo and banner: upload via POST /api/v1/files. Coordinates: latitude and longitude.")
    public ResponseEntity<ApiResponse<StoreResponse>> updateMyStore(@Valid @RequestBody CreateStoreRequest request,
                                                                    @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Store updated", storeService.updateMyStore(request, currentUser)));
    }

    @GetMapping("/products")
    @Operation(summary = "List my products", description = "My products, active and hidden; search title, brand or SKU.")
    public ResponseEntity<ApiResponse<PageResponse<ProductCardResponse>>> myProducts(
            @RequestParam(required = false) String q,
            @Parameter(description = "Category slug or id") @RequestParam(required = false) String category,
            @Parameter(description = "active | hidden") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Boolean active = status == null || status.isBlank() ? null : "active".equalsIgnoreCase(status);
        var params = new ProductService.SearchParams(q, category, null, null, null, null, null, null, null,
                null, null, sort);
        Long vendorId = currentUser.isAdmin() ? null : currentUser.getId();
        var filter = productService.toFilter(params, true, vendorId, active);
        return ResponseEntity.ok(ApiResponse.success("Products fetched", productService.search(filter, page, size)));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Get my product", description = "Full details, also when hidden.")
    public ResponseEntity<ApiResponse<ProductResponse>> myProduct(@PathVariable Long id,
                                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched", productService.getManagedProduct(id, currentUser)));
    }

    @PostMapping("/products")
    @Operation(summary = "Create a product", description = "Upload images via POST /api/v1/files first. Sellers waiting for approval create hidden products.")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody CreateProductRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created", productService.createProduct(request, currentUser)));
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Update my product", description = "Replaces every field.")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody CreateProductRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, request, currentUser)));
    }

    @PatchMapping("/products/{id}/status")
    @Operation(summary = "Show or hide my product", description = "status: active | hidden. Publishing requires an approved account.")
    public ResponseEntity<ApiResponse<ProductResponse>> status(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateProductStatusRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product status updated", productService.updateStatus(id, request, currentUser)));
    }

    @PatchMapping("/products/{id}/stock")
    @Operation(summary = "Set the stock", description = "Sets the stock quantity of my product.")
    public ResponseEntity<ApiResponse<ProductResponse>> stock(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateProductStockRequest request,
                                                              @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Stock updated", productService.updateStock(id, request, currentUser)));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Delete my product", description = "Soft delete: kept on past orders.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        productService.deleteProduct(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Product deleted"));
    }
}
