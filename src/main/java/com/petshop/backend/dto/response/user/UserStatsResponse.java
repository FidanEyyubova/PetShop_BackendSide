package com.petshop.backend.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Buyer dashboard KPI tiles")
public class UserStatsResponse {
    @Schema(description = "Orders in pending, processing or shipping")
    private long activeOrders;
    private long totalOrders;
    @Schema(description = "Sum of quantities in the cart")
    private long cartItems;
    private long wishlistItems;
    private long reviews;
    private long addresses;
}
