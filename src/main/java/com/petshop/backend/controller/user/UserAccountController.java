package com.petshop.backend.controller.user;

import com.petshop.backend.dto.request.user.NotificationPreferencesRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.user.NotificationPreferencesResponse;
import com.petshop.backend.dto.response.user.UserStatsResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.user.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@Tag(name = "Buyer Account", description = "Buyer dashboard: KPI tiles and notification settings")
public class UserAccountController {

    private final UserAccountService accountService;

    public UserAccountController(UserAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get my dashboard stats", description = "Active orders, total orders, cart items, wishlist items, reviews and addresses.")
    public ResponseEntity<ApiResponse<UserStatsResponse>> stats(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Stats fetched", accountService.getStats(currentUser)));
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get notification preferences", description = "Order, promotion and blog email toggles.")
    public ResponseEntity<ApiResponse<NotificationPreferencesResponse>> preferences(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Preferences fetched", accountService.getPreferences(currentUser)));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update notification preferences", description = "Fields that are not sent stay unchanged.")
    public ResponseEntity<ApiResponse<NotificationPreferencesResponse>> updatePreferences(
            @RequestBody NotificationPreferencesRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Preferences updated", accountService.updatePreferences(request, currentUser)));
    }
}
