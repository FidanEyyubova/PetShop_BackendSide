package com.petshop.backend.service.order.impl;

import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.entity.order.Wishlist;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.order.WishlistRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.WishlistService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponse> getUserWishlist(UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        List<Long> productIds = wishlists.stream().map(Wishlist::getProductId).collect(Collectors.toList());
        List<Product> products = productRepository.findAllById(productIds);

        return products.stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }

    @Override
    public boolean toggleWishlist(Long productId, UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("MÉ™hsul tapÄ±lmadÄ± ID: " + productId);
        }

        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            return false;
        } else {
            Wishlist wishlist = Wishlist.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();

            wishlistRepository.save(wishlist);
            return true;
        }
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .slug(product.getSlug())
                .sku(product.getSku())
                .brand(product.getBrand())
                .petType(product.getPetType())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .images(product.getImages())
                .rating(product.getRating())
                .build();
    }
}