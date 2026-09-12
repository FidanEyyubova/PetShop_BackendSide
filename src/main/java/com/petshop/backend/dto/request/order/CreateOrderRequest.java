package com.petshop.backend.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CreateOrderRequest {

    @NotBlank(message = "Ã‡atdÄ±rÄ±lma Ã¼nvanÄ± boÅŸ ola bilmÉ™z")
    private String shippingAddress;

    private String paymentMethod;

    private List<OrderItemRequest> items;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String shippingAddress, String paymentMethod, List<OrderItemRequest> items) {
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.items = items;
    }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}