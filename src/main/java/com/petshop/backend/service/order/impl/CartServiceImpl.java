package com.petshop.backend.service.order.impl;

import com.petshop.backend.dto.request.cart.AddToCartRequest;
import com.petshop.backend.dto.response.common.CartItemResponse;
import com.petshop.backend.dto.response.order.CartResponse;
import com.petshop.backend.entity.order.CartItem;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.order.CartItemRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(UserPrincipal currentUser) {
        List<CartItem> items = cartItemRepository.findByUserId(currentUser.getId());
        return mapToCartResponse(items);
    }

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request, UserPrincipal currentUser) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("MÉ™hsul tapÄ±lmadÄ±: " + request.getProductId()));

        CartItem existingItem = cartItemRepository.findByUserIdAndProductId(currentUser.getId(), request.getProductId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .userId(currentUser.getId())
                    .productId(product.getId())
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        return getCart(currentUser);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long itemId, Integer quantity, UserPrincipal currentUser) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("SÉ™bÉ™t mÉ™hsulu tapÄ±lmadÄ±: " + itemId));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return getCart(currentUser);
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(Long itemId, UserPrincipal currentUser) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("SÉ™bÉ™t mÉ™hsulu tapÄ±lmadÄ±: " + itemId));

        cartItemRepository.delete(cartItem);
        return getCart(currentUser);
    }

    @Override
    @Transactional
    public void clearCart(UserPrincipal currentUser) {
        cartItemRepository.deleteByUserId(currentUser.getId());
    }

    private CartResponse mapToCartResponse(List<CartItem> items) {
        List<CartItemResponse> itemResponses = items.stream().map(item -> {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            BigDecimal price = product != null ? product.getPrice() : BigDecimal.ZERO;
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            return CartItemResponse.builder()
                    .id(item.getId())
                    .userId(item.getUserId())
                    .productId(item.getProductId())
                    .productTitle(product != null ? product.getTitle() : "MÉ™hsul")
                    .productImage(product != null ? product.getImages() : null)
                    .unitPrice(price)
                    .productPrice(price)
                    .quantity(item.getQuantity())
                    .subtotal(subtotal)
                    .build();
        }).toList();

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(itemResponses)
                .totalAmount(totalAmount)
                .build();
    }
}