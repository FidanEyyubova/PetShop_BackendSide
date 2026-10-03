package com.petshop.backend.dto.response.order;

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
public class OrderItemResponse {
    private Long id;
    private Long productId;
    @Schema(description = "Title at checkout time")
    private String productTitle;
    @Schema(description = "Cover image at checkout time")
    private String productImage;
    @Schema(description = "Price paid per unit")
    private BigDecimal unitPrice;
    @Schema(description = "List price per unit at checkout")
    private BigDecimal originalPrice;
    private Integer quantity;
    @Schema(description = "unitPrice x quantity")
    private BigDecimal subtotal;
}
