package com.petshop.backend.service.order;

import com.petshop.backend.dto.response.order.WishlistItemResponse;
import com.petshop.backend.dto.response.order.WishlistResponse;
import com.petshop.backend.security.UserPrincipal;

public interface WishlistService {
    WishlistResponse getUserWishlist(UserPrincipal currentUser);
    WishlistItemResponse getWishlistItemById(Long itemId, UserPrincipal currentUser);
    WishlistResponse addToWishlist(Long productId, Integer quantity, UserPrincipal currentUser);
    WishlistResponse updateWishlistItemQuantity(Long itemId, Integer quantity, UserPrincipal currentUser);
    WishlistResponse removeFromWishlist(Long itemId, UserPrincipal currentUser);
    void clearWishlist(UserPrincipal currentUser);
    boolean toggleWishlist(Long productId, UserPrincipal currentUser);
}