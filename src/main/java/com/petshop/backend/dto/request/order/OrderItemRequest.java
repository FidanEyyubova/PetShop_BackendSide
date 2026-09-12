package com.petshop.backend.dto.request.order;

import jakarta.validation.constraints.NotNull;

public class OrderItemRequest {

    @NotNull(message = "MÉ™hsul ID-si boÅŸ ola bilmÉ™z")
    private Long productId;

    @NotNull(message = "Miqdar boÅŸ ola bilmÉ™z")
    private Integer quantity;

    public OrderItemRequest() {
    }

    public OrderItemRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}