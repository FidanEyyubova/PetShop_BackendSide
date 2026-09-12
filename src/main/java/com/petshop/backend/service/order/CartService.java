package com.petshop.backend.service.order;

import com.petshop.backend.dto.request.cart.AddToCartRequest;
import com.petshop.backend.dto.request.cart.UpdateCartItemRequest;
import com.petshop.backend.dto.response.common.CartItemResponse;
import com.petshop.backend.dto.response.order.CartResponse;
import com.petshop.backend.security.UserPrincipal;

import java.util.List;

public interface CartService {
    CartResponse getCart(UserPrincipal currentUser);
    CartResponse addToCart(AddToCartRequest request, UserPrincipal currentUser);
    CartResponse updateCartItemQuantity(Long itemId, Integer quantity, UserPrincipal currentUser);
    CartResponse removeFromCart(Long itemId, UserPrincipal currentUser);
    void clearCart(UserPrincipal currentUser);
}