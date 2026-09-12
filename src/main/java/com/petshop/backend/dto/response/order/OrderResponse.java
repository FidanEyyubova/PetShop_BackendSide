package com.petshop.backend.dto.response.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long id;
    private Long buyerId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse() {
    }

    public OrderResponse(Long id, Long buyerId, String orderNumber, BigDecimal totalAmount, String status, String paymentStatus, String shippingAddress, LocalDateTime createdAt, List<OrderItemResponse> items) {
        this.id = id;
        this.buyerId = buyerId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.items = items;
    }

    public static OrderResponseBuilder builder() {
        return new OrderResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    public static class OrderResponseBuilder {
        private Long id;
        private Long buyerId;
        private String orderNumber;
        private BigDecimal totalAmount;
        private String status;
        private String paymentStatus;
        private String shippingAddress;
        private LocalDateTime createdAt;
        private List<OrderItemResponse> items;

        OrderResponseBuilder() {
        }

        public OrderResponseBuilder id(Long id) { this.id = id; return this; }
        public OrderResponseBuilder buyerId(Long buyerId) { this.buyerId = buyerId; return this; }
        public OrderResponseBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public OrderResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderResponseBuilder status(String status) { this.status = status; return this; }
        public OrderResponseBuilder paymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public OrderResponseBuilder shippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; return this; }
        public OrderResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public OrderResponseBuilder items(List<OrderItemResponse> items) { this.items = items; return this; }

        public OrderResponse build() {
            return new OrderResponse(id, buyerId, orderNumber, totalAmount, status, paymentStatus, shippingAddress, createdAt, items);
        }
    }
}