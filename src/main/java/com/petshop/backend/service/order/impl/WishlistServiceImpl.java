package com.petshop.backend.service.order.impl;

import com.petshop.backend.dto.response.order.WishlistItemResponse;
import com.petshop.backend.dto.response.order.WishlistResponse;
import com.petshop.backend.entity.order.Wishlist;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.order.WishlistRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse getUserWishlist(UserPrincipal currentUser) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(currentUser.getId());
        return mapToWishlistResponse(wishlists);
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistItemResponse getWishlistItemById(Long itemId, UserPrincipal currentUser) {
        Wishlist wishlist = wishlistRepository.findByIdAndUserId(itemId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found with ID: " + itemId));
        return mapToItemResponse(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse addToWishlist(Long productId, Integer quantity, UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        int qty = (quantity != null && quantity > 0) ? quantity : 1;
        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndProductId(userId, productId);

        if (existing.isPresent()) {
            Wishlist item = existing.get();
            item.setQuantity(item.getQuantity() + qty);
            wishlistRepository.save(item);
        } else {
            Wishlist item = Wishlist.builder()
                    .userId(userId)
                    .productId(productId)
                    .quantity(qty)
                    .createdAt(LocalDateTime.now())
                    .build();
            wishlistRepository.save(item);
        }

        return getUserWishlist(currentUser);
    }

    @Override
    @Transactional
    public WishlistResponse updateWishlistItemQuantity(Long itemId, Integer quantity, UserPrincipal currentUser) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be at least 1");
        }

        Wishlist wishlist = wishlistRepository.findByIdAndUserId(itemId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found with ID: " + itemId));

        wishlist.setQuantity(quantity);
        wishlistRepository.save(wishlist);
        return getUserWishlist(currentUser);
    }

    @Override
    @Transactional
    public WishlistResponse removeFromWishlist(Long itemId, UserPrincipal currentUser) {
        Wishlist wishlist = wishlistRepository.findByIdAndUserId(itemId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found with ID: " + itemId));

        wishlistRepository.delete(wishlist);
        return getUserWishlist(currentUser);
    }

    @Override
    @Transactional
    public void clearWishlist(UserPrincipal currentUser) {
        wishlistRepository.deleteByUserId(currentUser.getId());
    }

    @Override
    @Transactional
    public boolean toggleWishlist(Long productId, UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }

        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            return false;
        } else {
            Wishlist wishlist = Wishlist.builder()
                    .userId(userId)
                    .productId(productId)
                    .quantity(1)
                    .createdAt(LocalDateTime.now())
                    .build();
            wishlistRepository.save(wishlist);
            return true;
        }
    }

    private WishlistResponse mapToWishlistResponse(List<Wishlist> wishlists) {
        List<WishlistItemResponse> items = new ArrayList<>();
        for (Wishlist w : wishlists) {
            WishlistItemResponse itemResp = mapToItemResponse(w);
            if (itemResp != null) {
                items.add(itemResp);
            }
        }
        return WishlistResponse.builder()
                .items(items)
                .totalItems(items.size())
                .build();
    }

    private WishlistItemResponse mapToItemResponse(Wishlist wishlist) {
        Product p = productRepository.findById(wishlist.getProductId()).orElse(null);
        if (p == null) return null;

        return WishlistItemResponse.builder()
                .id(wishlist.getId())
                .productId(p.getId())
                .productTitle(p.getTitle())
                .productImage(p.getImages())
                .price(p.getPrice())
                .discountPrice(p.getDiscountPrice())
                .quantity(wishlist.getQuantity() != null ? wishlist.getQuantity() : 1)
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
}