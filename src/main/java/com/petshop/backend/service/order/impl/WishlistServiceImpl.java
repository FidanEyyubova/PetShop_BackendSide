package com.petshop.backend.service.order.impl;

import com.petshop.backend.dto.request.order.MergeWishlistRequest;
import com.petshop.backend.dto.response.order.WishlistItemResponse;
import com.petshop.backend.dto.response.order.WishlistResponse;
import com.petshop.backend.dto.response.order.WishlistToggleResponse;
import com.petshop.backend.dto.response.product.ProductCardResponse;
import com.petshop.backend.entity.order.Wishlist;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.order.WishlistRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.WishlistService;
import com.petshop.backend.service.product.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, ProductRepository productRepository,
                               ProductMapper productMapper) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse getUserWishlist(UserPrincipal currentUser) {
        return build(CurrentUser.require(currentUser).getId());
    }

    /** Idempotent: adding a product twice keeps one row. */
    @Override
    @Transactional
    public WishlistResponse addToWishlist(Long productId, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        visibleProduct(productId);
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            wishlistRepository.save(Wishlist.builder().userId(userId).productId(productId).build());
        }
        return build(userId);
    }

    @Override
    @Transactional
    public WishlistResponse removeProduct(Long productId, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        return build(userId);
    }

    @Override
    @Transactional
    public WishlistResponse removeItem(Long itemId, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        Wishlist item = wishlistRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item", "id", itemId));
        wishlistRepository.delete(item);
        return build(userId);
    }

    @Override
    @Transactional
    public WishlistToggleResponse toggleWishlist(Long productId, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        boolean added;
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            added = false;
        } else {
            visibleProduct(productId);
            wishlistRepository.save(Wishlist.builder().userId(userId).productId(productId).build());
            added = true;
        }
        return new WishlistToggleResponse(productId, added, wishlistRepository.countByUserId(userId));
    }

    @Override
    @Transactional
    public WishlistResponse merge(MergeWishlistRequest request, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        Set<Long> ids = new LinkedHashSet<>(request.getProductIds());
        Set<Long> visible = productRepository.findAllById(ids).stream()
                .filter(Product::isPubliclyVisible).map(Product::getId).collect(Collectors.toSet());
        for (Long id : ids) {
            if (visible.contains(id) && !wishlistRepository.existsByUserIdAndProductId(userId, id)) {
                wishlistRepository.save(Wishlist.builder().userId(userId).productId(id).build());
            }
        }
        return build(userId);
    }

    @Override
    @Transactional
    public void clearWishlist(UserPrincipal currentUser) {
        wishlistRepository.deleteByUserId(CurrentUser.require(currentUser).getId());
    }

    private Product visibleProduct(Long productId) {
        return productRepository.findById(productId)
                .filter(Product::isPubliclyVisible)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    /** Deleted or hidden products are left out of the response. */
    private WishlistResponse build(Long userId) {
        List<Wishlist> rows = wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
        Map<Long, Product> products = productRepository.findAllById(rows.stream().map(Wishlist::getProductId).toList())
                .stream().filter(Product::isPubliclyVisible)
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        ProductMapper.Context ctx = productMapper.context(products.values());
        List<WishlistItemResponse> items = new ArrayList<>();
        for (Wishlist w : rows) {
            Product p = products.get(w.getProductId());
            if (p == null) continue;
            ProductCardResponse card = productMapper.toCard(p, ctx);
            items.add(WishlistItemResponse.builder()
                    .id(w.getId())
                    .productId(p.getId())
                    .addedAt(w.getCreatedAt())
                    .product(card)
                    .build());
        }
        return WishlistResponse.builder()
                .items(items)
                .productIds(items.stream().map(WishlistItemResponse::getProductId).toList())
                .totalItems(items.size())
                .build();
    }
}
