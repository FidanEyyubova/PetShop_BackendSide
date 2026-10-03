package com.petshop.backend.controller.user;

import com.petshop.backend.dto.request.user.AddressRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.user.UserAddressResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.user.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/addresses")
@Tag(name = "Buyer Addresses", description = "Delivery addresses; exactly one default address per user")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "List my addresses", description = "The default address comes first.")
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> list(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched", addressService.getUserAddresses(currentUser)));
    }

    @PostMapping
    @Operation(summary = "Add an address", description = "The first address becomes the default.")
    public ResponseEntity<ApiResponse<UserAddressResponse>> create(@Valid @RequestBody AddressRequest request,
                                                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added", addressService.createAddress(request, currentUser)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address")
    public ResponseEntity<ApiResponse<UserAddressResponse>> update(@PathVariable Long id,
                                                                   @Valid @RequestBody AddressRequest request,
                                                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Address updated", addressService.updateAddress(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address", description = "If it was the default, the next address becomes default.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        addressService.deleteAddress(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Address deleted"));
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "Make an address the default")
    public ResponseEntity<ApiResponse<UserAddressResponse>> setDefault(@PathVariable Long id,
                                                                       @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Default address set", addressService.setDefaultAddress(id, currentUser)));
    }
}
