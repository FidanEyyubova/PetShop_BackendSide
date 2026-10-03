package com.petshop.backend.service.order;

import com.petshop.backend.dto.request.cart.AddToCartRequest;
import com.petshop.backend.dto.request.cart.MergeCartRequest;
import com.petshop.backend.dto.response.order.CartResponse;
import com.petshop.backend.security.UserPrincipal;

public interface CartService {
    CartResponse getCart(UserPrincipal currentUser);
    CartResponse addToCart(AddToCartRequest request, UserPrincipal currentUser);
    CartResponse updateCartItemQuantity(Long itemId, Integer quantity, UserPrincipal currentUser);
    CartResponse removeFromCart(Long itemId, UserPrincipal currentUser);
    CartResponse mergeCart(MergeCartRequest request, UserPrincipal currentUser);
    void clearCart(UserPrincipal currentUser);
}
