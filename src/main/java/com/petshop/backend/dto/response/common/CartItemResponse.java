package com.petshop.backend.dto.response.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    @Schema(description = "Cart line id (use for PUT/DELETE /cart/items/{id})")
    private Long id;
    private Long productId;
    private String productTitle;
    private String productSlug;
    private String productImage;
    private Long storeId;
    private String storeName;

    @Schema(description = "Price per unit the customer pays (discount applied)")
    private BigDecimal unitPrice;
    @Schema(description = "List price per unit")
    private BigDecimal originalPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    private Integer stockQuantity;
    @Schema(description = "false when the product is hidden, deleted or the quantity exceeds stock")
    private Boolean available;
    @Schema(description = "Why the line is not available, e.g. 'Only 3 left'")
    private String availabilityMessage;
}
