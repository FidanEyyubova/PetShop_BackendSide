package com.petshop.backend.controller.admin;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.admin.UpdatePayoutStatusRequest;
import com.petshop.backend.dto.request.admin.UpdateStoreVerificationRequest;
import com.petshop.backend.dto.request.admin.UpdateUserStatusRequest;
import com.petshop.backend.dto.request.coupon.CouponRequest;
import com.petshop.backend.dto.request.vendor.UpdateOrderStatusRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.content.NewsletterSubscriberResponse;
import com.petshop.backend.dto.response.coupon.CouponAdminResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.dto.response.user.UserResponse;
import com.petshop.backend.dto.response.vendor.VendorPayoutResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.admin.AdminManagementService;
import com.petshop.backend.service.content.NewsletterService;
import com.petshop.backend.service.coupon.CouponService;
import com.petshop.backend.service.vendor.VendorOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('" + Roles.ADMIN + "')")
@Tag(name = "Admin - Operations", description = "Seller approval, accounts, orders, payouts, stores, coupons, newsletter")
public class AdminManagementController {

    private final AdminManagementService adminService;
    private final VendorOrderService orderService;
    private final CouponService couponService;
    private final NewsletterService newsletterService;

    public AdminManagementController(AdminManagementService adminService, VendorOrderService orderService,
                                     CouponService couponService, NewsletterService newsletterService) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.couponService = couponService;
        this.newsletterService = newsletterService;
    }

    @GetMapping("/users")
    @Operation(summary = "List users", description = "Filter by role and status, e.g. status=PENDING_APPROVAL for the seller approval queue.")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> users(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Users fetched", adminService.users(role, status, q, page, size)));
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Approve, reject or suspend an account",
            description = "Works for vendors and service providers. ACTIVE approves; REJECTED or SUSPENDED signs the user out "
                    + "and hides their products or services. The user is notified by email.")
    public ResponseEntity<ApiResponse<UserResponse>> userStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User status updated", adminService.updateUserStatus(id, request)));
    }

    @GetMapping("/orders")
    @Operation(summary = "List all orders", description = "Orders of every store; filter by status, month (yyyy-MM) and search text.")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> orders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched",
                orderService.getVendorOrders(status, month, q, true, page, size, currentUser)));
    }

    @PutMapping("/orders/{id}/status")
    @Operation(summary = "Change an order status", description = "Same rules as for vendors: pending -> processing -> shipping -> delivered; cancel from pending or processing.")
    public ResponseEntity<ApiResponse<OrderResponse>> orderStatus(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdateOrderStatusRequest request,
                                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated", orderService.updateOrderStatus(id, request, currentUser)));
    }

    @GetMapping("/payouts")
    @Operation(summary = "List payout requests", description = "Vendor withdrawal requests; filter by status. Full IBAN is shown to admins.")
    public ResponseEntity<ApiResponse<PageResponse<VendorPayoutResponse>>> payouts(
            @Parameter(description = "PENDING, APPROVED, COMPLETED, REJECTED") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Payouts fetched", adminService.payouts(status, page, size)));
    }

    @PatchMapping("/payouts/{id}/status")
    @Operation(summary = "Approve, complete or reject a payout")
    public ResponseEntity<ApiResponse<VendorPayoutResponse>> payoutStatus(@PathVariable Long id,
                                                                          @Valid @RequestBody UpdatePayoutStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payout updated", adminService.updatePayoutStatus(id, request)));
    }

    @PatchMapping("/stores/{id}/verification")
    @Operation(summary = "Set a store's verified badge, badge text and response rate")
    public ResponseEntity<ApiResponse<StoreResponse>> verifyStore(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdateStoreVerificationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Store updated", adminService.updateStoreVerification(id, request)));
    }

    @GetMapping("/coupons")
    @Operation(summary = "List coupons", description = "All coupons with usage counts.")
    public ResponseEntity<ApiResponse<List<CouponAdminResponse>>> coupons() {
        return ResponseEntity.ok(ApiResponse.success("Coupons fetched", couponService.getAll()));
    }

    @PostMapping("/coupons")
    @Operation(summary = "Create a coupon", description = "PERCENTAGE or FIXED discount with optional minimum, cap, validity and usage limit.")
    public ResponseEntity<ApiResponse<CouponAdminResponse>> createCoupon(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Coupon created", couponService.create(request)));
    }

    @PutMapping("/coupons/{id}")
    @Operation(summary = "Update a coupon", description = "Replaces the coupon's fields.")
    public ResponseEntity<ApiResponse<CouponAdminResponse>> updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Coupon updated", couponService.update(id, request)));
    }

    @DeleteMapping("/coupons/{id}")
    @Operation(summary = "Delete a coupon", description = "Removes the coupon.")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted"));
    }

    @GetMapping("/newsletter")
    @Operation(summary = "List newsletter subscribers", description = "Newest first.")
    public ResponseEntity<ApiResponse<PageResponse<NewsletterSubscriberResponse>>> newsletter(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.success("Subscribers fetched", newsletterService.list(page, size)));
    }
}
