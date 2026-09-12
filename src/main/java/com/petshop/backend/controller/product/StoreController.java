package com.petshop.backend.controller.product;

import com.petshop.backend.dto.request.product.CreateStoreRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.product.StoreService;
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
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StoreResponse>>> getAllStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<StoreResponse> response = storeService.getAllStores(pageable);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<StoreResponse>>builder()
                        .success(true)
                        .message("MaÄŸazalar gÉ™tirildi")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(@PathVariable Long id) {
        StoreResponse response = storeService.getStoreById(id);
        return ResponseEntity.ok(
                ApiResponse.<StoreResponse>builder()
                        .success(true)
                        .message("MaÄŸaza detalÄ±")
                        .data(response)
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @Valid @RequestBody CreateStoreRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StoreResponse response = storeService.createStore(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<StoreResponse>builder()
                        .success(true)
                        .message("MaÄŸaza yaradÄ±ldÄ±")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody CreateStoreRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StoreResponse response = storeService.updateStore(id, request, currentUser);
        return ResponseEntity.ok(
                ApiResponse.<StoreResponse>builder()
                        .success(true)
                        .message("MaÄŸaza yenilÉ™ndi")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStore(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        storeService.deleteStore(id, currentUser);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("MaÄŸaza silindi")
                        .data(null)
                        .build()
        );
    }
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }
}