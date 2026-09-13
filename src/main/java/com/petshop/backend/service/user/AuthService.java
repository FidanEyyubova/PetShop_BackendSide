package com.petshop.backend.service.user;

import com.petshop.backend.dto.request.auth.*;
import com.petshop.backend.dto.request.user.ChangePasswordRequest;
import com.petshop.backend.dto.request.user.UpdateProfileRequest;
import com.petshop.backend.dto.response.auth.AuthResponse;
import com.petshop.backend.dto.response.user.UserResponse;
import com.petshop.backend.security.UserPrincipal;

public interface AuthService {
    AuthResponse registerBuyer(RegisterBuyerRequest request);
    AuthResponse registerVendor(RegisterVendorRequest request);
    AuthResponse registerServiceProvider(RegisterServiceProviderRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    UserResponse getCurrentUser(UserPrincipal currentUser);
    UserResponse updateProfile(UpdateProfileRequest request, UserPrincipal currentUser);
    void changePassword(ChangePasswordRequest request, UserPrincipal currentUser);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}