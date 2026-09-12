package com.petshop.backend.dto.response.order;

import com.petshop.backend.dto.response.common.CartItemResponse;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;

    public CartResponse() {
    }

    public CartResponse(List<CartItemResponse> items, BigDecimal totalAmount) {
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public static CartResponseBuilder builder() {
        return new CartResponseBuilder();
    }

    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public static class CartResponseBuilder {
        private List<CartItemResponse> items;
        private BigDecimal totalAmount;

        CartResponseBuilder() {
        }

        public CartResponseBuilder items(List<CartItemResponse> items) { this.items = items; return this; }
        public CartResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }

        public CartResponse build() {
            return new CartResponse(items, totalAmount);
        }
    }
}