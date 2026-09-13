package com.petshop.backend.controller.user;

import com.petshop.backend.dto.request.user.AddressRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.user.UserAddressResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.user.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/addresses")
@Tag(name = "Address Controller", description = "User delivery address management")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "Get list of user delivery addresses")
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> getUserAddresses(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<UserAddressResponse> response = addressService.getUserAddresses(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched successfully", response));
    }

    @PostMapping
    @Operation(summary = "Add a new delivery address")
    public ResponseEntity<ApiResponse<UserAddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        UserAddressResponse response = addressService.createAddress(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Address created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit an existing delivery address")
    public ResponseEntity<ApiResponse<UserAddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        UserAddressResponse response = addressService.updateAddress(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a delivery address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        addressService.deleteAddress(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "Set address as primary default address")
    public ResponseEntity<ApiResponse<UserAddressResponse>> setDefaultAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        UserAddressResponse response = addressService.setDefaultAddress(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Default address updated successfully", response));
    }
}