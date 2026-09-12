package com.petshop.backend.dto.response.order;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productImage;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    public OrderItemResponse() {
    }

    public OrderItemResponse(Long id, Long productId, String productTitle, String productImage, BigDecimal unitPrice, Integer quantity, BigDecimal subtotal) {
        this.id = id;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productImage = productImage;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public static OrderItemResponseBuilder builder() {
        return new OrderItemResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public static class OrderItemResponseBuilder {
        private Long id;
        private Long productId;
        private String productTitle;
        private String productImage;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;

        OrderItemResponseBuilder() {
        }

        public OrderItemResponseBuilder id(Long id) { this.id = id; return this; }
        public OrderItemResponseBuilder productId(Long productId) { this.productId = productId; return this; }
        public OrderItemResponseBuilder productTitle(String productTitle) { this.productTitle = productTitle; return this; }
        public OrderItemResponseBuilder productImage(String productImage) { this.productImage = productImage; return this; }
        public OrderItemResponseBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public OrderItemResponseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public OrderItemResponseBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }

        public OrderItemResponse build() {
            return new OrderItemResponse(id, productId, productTitle, productImage, unitPrice, quantity, subtotal);
        }
    }
}