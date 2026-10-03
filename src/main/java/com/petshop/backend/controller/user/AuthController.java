package com.petshop.backend.controller.user;

import com.petshop.backend.dto.request.auth.*;
import com.petshop.backend.dto.request.user.ChangePasswordRequest;
import com.petshop.backend.dto.request.user.DeleteAccountRequest;
import com.petshop.backend.dto.request.user.UpdateProfileRequest;
import com.petshop.backend.dto.response.auth.AuthResponse;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.user.UserResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.user.AuthService;
import com.petshop.backend.service.user.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Auth", description = "Registration (customer, vendor, service provider), login, tokens, profile and password")
public class AuthController {

    private final AuthService authService;
    private final UserAccountService accountService;

    public AuthController(AuthService authService, UserAccountService accountService) {
        this.authService = authService;
        this.accountService = accountService;
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Sign in", description = "Returns a 60-minute access token and a refresh token. rememberMe=true keeps the refresh token for 30 days. Wrong credentials return 401.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Signed in successfully", authService.login(request)));
    }

    @PostMapping("/auth/google")
    @Operation(summary = "Sign in with Google",
            description = "Send the ID token (`credential`) from the Google Sign-In button. A buyer account is created on first "
                    + "sign-in; an existing account with the same email is signed in. Requires GOOGLE_CLIENT_ID on the server.")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Signed in with Google", authService.loginWithGoogle(request)));
    }

    @PostMapping("/auth/register")
    @Operation(summary = "Register a customer (buyer)", description = "Duplicate email -> 409.")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterBuyerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", authService.registerBuyer(request)));
    }

    @PostMapping("/auth/register/vendor")
    @Operation(summary = "Register a vendor and create the store",
            description = "Account status is PENDING_APPROVAL until an admin approves it; the vendor can sign in "
                    + "and edit the store, but cannot publish products before approval.")
    public ResponseEntity<ApiResponse<AuthResponse>> registerVendor(@Valid @RequestBody RegisterVendorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vendor registered; waiting for admin approval", authService.registerVendor(request)));
    }

    @PostMapping("/auth/register/provider")
    @Operation(summary = "Register a service provider and create the center profile",
            description = "Account status is PENDING_APPROVAL; the center is listed publicly after admin approval.")
    public ResponseEntity<ApiResponse<AuthResponse>> registerProvider(@Valid @RequestBody RegisterServiceProviderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service provider registered; waiting for admin approval",
                        authService.registerServiceProvider(request)));
    }

    @PostMapping("/auth/refresh-token")
    @Operation(summary = "Refresh the access token", description = "Send the refresh token; it is rotated, so use the new refreshToken from the response next time.")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authService.refreshToken(request)));
    }

    @PostMapping("/auth/logout")
    @Operation(summary = "Sign out", description = "Revokes the given refresh token; allDevices=true (with an access token) signs out every session.")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) LogoutRequest request,
                                                    @AuthenticationPrincipal UserPrincipal currentUser) {
        authService.logout(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Signed out"));
    }

    @PostMapping("/auth/forgot-password")
    @Operation(summary = "Request a password reset code",
            description = "Always returns 200. When the email is registered a 6-digit code (valid 15 minutes) is emailed.")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("If this email is registered, a reset code has been sent"));
    }

    @PostMapping("/auth/reset-password")
    @Operation(summary = "Reset the password with the emailed code",
            description = "5 wrong attempts invalidate the code. All sessions are signed out and a confirmation email is sent.")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password has been reset"));
    }

    @GetMapping("/auth/me")
    @Operation(summary = "Get my profile", description = "The signed-in user, including role, status and (for vendors) storeId.")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Current user", authService.getCurrentUser(currentUser)));
    }

    @PutMapping({"/auth/me", "/users/profile"})
    @Operation(summary = "Update my profile", description = "Partial update: fields that are not sent stay unchanged. Upload the avatar via POST /api/v1/files first.")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", authService.updateProfile(request, currentUser)));
    }

    @PutMapping("/auth/change-password")
    @Operation(summary = "Change my password", description = "Requires the current password; other sessions are signed out and a confirmation email is sent.")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                            @AuthenticationPrincipal UserPrincipal currentUser) {
        authService.changePassword(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Password changed"));
    }

    @DeleteMapping("/auth/me")
    @Operation(summary = "Delete my account",
            description = "Requires the current password. Not allowed while orders are in progress (409). "
                    + "Personal data is anonymised; past orders and reviews are kept anonymously.")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@Valid @RequestBody DeleteAccountRequest request,
                                                           @AuthenticationPrincipal UserPrincipal currentUser) {
        accountService.deleteAccount(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Account deleted"));
    }
}
