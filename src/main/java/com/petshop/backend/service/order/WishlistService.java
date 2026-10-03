package com.petshop.backend.service.order;

import com.petshop.backend.dto.request.order.MergeWishlistRequest;
import com.petshop.backend.dto.response.order.WishlistResponse;
import com.petshop.backend.dto.response.order.WishlistToggleResponse;
import com.petshop.backend.security.UserPrincipal;

public interface WishlistService {
    WishlistResponse getUserWishlist(UserPrincipal currentUser);
    WishlistResponse addToWishlist(Long productId, UserPrincipal currentUser);
    WishlistResponse removeProduct(Long productId, UserPrincipal currentUser);
    WishlistResponse removeItem(Long itemId, UserPrincipal currentUser);
    WishlistToggleResponse toggleWishlist(Long productId, UserPrincipal currentUser);
    WishlistResponse merge(MergeWishlistRequest request, UserPrincipal currentUser);
    void clearWishlist(UserPrincipal currentUser);
}
