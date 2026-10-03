package com.petshop.backend.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Order line with a snapshot of the product at checkout time. */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "product_title")
    private String productTitle;

    @Column(name = "product_image", columnDefinition = "TEXT")
    private String productImage;

    /** List price at checkout. */
    @Column(name = "original_price")
    private BigDecimal originalPrice;

    /** Price paid per unit (discount applied). */
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal subtotal;
}
