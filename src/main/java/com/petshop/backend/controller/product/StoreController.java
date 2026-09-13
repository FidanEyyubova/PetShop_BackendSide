package com.petshop.backend.controller.product;

import com.petshop.backend.dto.request.product.CreateStoreRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.product.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "Store Controller", description = "Store profiles, vendor listings, and store details management")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    @Operation(summary = "Get list of all stores with pagination")
    public ResponseEntity<ApiResponse<PageResponse<StoreResponse>>> getAllStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<StoreResponse> response = storeService.getAllStores(pageable);
        return ResponseEntity.ok(ApiResponse.success("Stores fetched successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get store details by ID")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(@PathVariable Long id) {
        StoreResponse response = storeService.getStoreById(id);
        return ResponseEntity.ok(ApiResponse.success("Store details fetched successfully", response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    @Operation(summary = "Create a new vendor store")
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @Valid @RequestBody CreateStoreRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StoreResponse response = storeService.createStore(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Store created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    @Operation(summary = "Update vendor store profile details")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody CreateStoreRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StoreResponse response = storeService.updateStore(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Store updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    @Operation(summary = "Delete vendor store profile")
    public ResponseEntity<ApiResponse<Void>> deleteStore(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        storeService.deleteStore(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Store deleted successfully"));
    }
}