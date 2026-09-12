package com.petshop.backend.entity.order;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlists")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;
    private LocalDateTime createdAt;

    public Wishlist() {}

    public Wishlist(Long id, Long userId, Long productId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static WishlistBuilder builder() { return new WishlistBuilder(); }

    public static class WishlistBuilder {
        private Long id;
        private Long userId;
        private Long productId;
        private LocalDateTime createdAt;

        public WishlistBuilder id(Long id) { this.id = id; return this; }
        public WishlistBuilder userId(Long userId) { this.userId = userId; return this; }
        public WishlistBuilder productId(Long productId) { this.productId = productId; return this; }
        public WishlistBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Wishlist build() {
            return new Wishlist(id, userId, productId, createdAt);
        }
    }
}