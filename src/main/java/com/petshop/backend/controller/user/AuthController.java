package com.petshop.backend.controller.user;

import com.petshop.backend.dto.request.auth.LoginRequest;
import com.petshop.backend.dto.request.auth.RegisterBuyerRequest;
import com.petshop.backend.dto.request.auth.RegisterServiceProviderRequest;
import com.petshop.backend.dto.request.auth.RegisterVendorRequest;
import com.petshop.backend.dto.response.auth.AuthResponse;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.user.UserResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.user.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/buyer")
    public ResponseEntity<ApiResponse<AuthResponse>> registerBuyer(@Valid @RequestBody RegisterBuyerRequest request) {
        AuthResponse response = authService.registerBuyer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("AlÄ±cÄ± uÄŸurla qeydiyyatdan keÃ§di")
                        .data(response)
                        .build());
    }

    @PostMapping("/register/vendor")
    public ResponseEntity<ApiResponse<AuthResponse>> registerVendor(@Valid @RequestBody RegisterVendorRequest request) {
        AuthResponse response = authService.registerVendor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("SatÄ±cÄ± uÄŸurla qeydiyyatdan keÃ§di")
                        .data(response)
                        .build());
    }

    @PostMapping("/register/service-provider")
    public ResponseEntity<ApiResponse<AuthResponse>> registerServiceProvider(@Valid @RequestBody RegisterServiceProviderRequest request) {
        AuthResponse response = authService.registerServiceProvider(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("XidmÉ™t tÉ™minatÃ§Ä±sÄ± uÄŸurla qeydiyyatdan keÃ§di")
                        .data(response)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("UÄŸurla daxil olundu")
                        .data(response)
                        .build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        UserResponse response = authService.getCurrentUser(currentUser);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("Cari istifadÉ™Ã§i mÉ™lumatlarÄ±")
                        .data(response)
                        .build());
    }
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
}