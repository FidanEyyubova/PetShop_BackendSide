package com.petshop.backend.dto.response.common;

import java.math.BigDecimal;

public class CartItemResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private String productTitle;
    private BigDecimal productPrice;
    private String productImage;
    private Integer quantity;
    private BigDecimal subtotal;

    public CartItemResponse() {
    }

    public CartItemResponse(Long id, Long userId, Long productId, String productTitle, BigDecimal productPrice, String productImage, Integer quantity, BigDecimal subtotal) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public static CartItemResponseBuilder builder() {
        return new CartItemResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }

    public BigDecimal getUnitPrice() { return productPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.productPrice = unitPrice; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public static class CartItemResponseBuilder {
        private Long id;
        private Long userId;
        private Long productId;
        private String productTitle;
        private BigDecimal productPrice;
        private String productImage;
        private Integer quantity;
        private BigDecimal subtotal;

        CartItemResponseBuilder() {
        }

        public CartItemResponseBuilder id(Long id) { this.id = id; return this; }
        public CartItemResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public CartItemResponseBuilder productId(Long productId) { this.productId = productId; return this; }
        public CartItemResponseBuilder productTitle(String productTitle) { this.productTitle = productTitle; return this; }
        public CartItemResponseBuilder productPrice(BigDecimal productPrice) { this.productPrice = productPrice; return this; }
        public CartItemResponseBuilder unitPrice(BigDecimal unitPrice) { this.productPrice = unitPrice; return this; }
        public CartItemResponseBuilder productImage(String productImage) { this.productImage = productImage; return this; }
        public CartItemResponseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public CartItemResponseBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }

        public CartItemResponse build() {
            return new CartItemResponse(id, userId, productId, productTitle, productPrice, productImage, quantity, subtotal);
        }
    }
}