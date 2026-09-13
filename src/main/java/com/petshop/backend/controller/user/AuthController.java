package com.petshop.backend.controller.user;

import com.petshop.backend.dto.request.auth.*;
import com.petshop.backend.dto.request.user.ChangePasswordRequest;
import com.petshop.backend.dto.request.user.UpdateProfileRequest;
import com.petshop.backend.dto.response.auth.AuthResponse;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.user.UserResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.user.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Auth Controller", description = "Authentication, Password Reset, and Profile Management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    @Operation(summary = "User login with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/auth/register")
    @Operation(summary = "Register a new buyer account")
    public ResponseEntity<ApiResponse<AuthResponse>> registerBuyer(@Valid @RequestBody RegisterBuyerRequest request) {
        AuthResponse response = authService.registerBuyer(request);
        return ResponseEntity.ok(ApiResponse.success("Buyer registration successful", response));
    }

    @PostMapping("/auth/register/vendor")
    @Operation(summary = "Register a new vendor account")
    public ResponseEntity<ApiResponse<AuthResponse>> registerVendor(@Valid @RequestBody RegisterVendorRequest request) {
        AuthResponse response = authService.registerVendor(request);
        return ResponseEntity.ok(ApiResponse.success("Vendor registration successful", response));
    }

    @PostMapping("/auth/register/provider")
    @Operation(summary = "Register a new service provider account")
    public ResponseEntity<ApiResponse<AuthResponse>> registerServiceProvider(@Valid @RequestBody RegisterServiceProviderRequest request) {
        AuthResponse response = authService.registerServiceProvider(request);
        return ResponseEntity.ok(ApiResponse.success("Service provider registration successful", response));
    }

    @PostMapping("/auth/refresh-token")
    @Operation(summary = "Refresh JWT access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/auth/forgot-password")
    @Operation(summary = "Send password reset verification code to email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset code sent to email successfully", null));
    }

    @PostMapping("/auth/reset-password")
    @Operation(summary = "Reset password using verification code and new password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @GetMapping("/auth/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        UserResponse response = authService.getCurrentUser(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", response));
    }

    @PutMapping({"/auth/me", "/users/profile"})
    @Operation(summary = "Update user profile details")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        UserResponse response = authService.updateProfile(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PutMapping("/auth/change-password")
    @Operation(summary = "Change user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        authService.changePassword(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}