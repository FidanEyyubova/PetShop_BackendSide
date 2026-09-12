package com.petshop.backend.service.order;

import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.security.UserPrincipal;
import java.util.List;

public interface WishlistService {
    List<ProductResponse> getUserWishlist(UserPrincipal currentUser);
    boolean toggleWishlist(Long productId, UserPrincipal currentUser);
}
