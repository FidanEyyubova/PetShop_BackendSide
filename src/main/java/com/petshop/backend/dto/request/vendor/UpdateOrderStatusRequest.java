package com.petshop.backend.dto.request.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateOrderStatusRequest {

    @NotBlank(message = "Status is required")
    @Schema(description = "processing, shipping, delivered or cancelled. Allowed: pending->processing->shipping->delivered; "
            + "cancel from pending/processing", example = "processing")
    private String status;

    @Size(max = 100)
    @Schema(description = "Courier shown to the buyer (usually with SHIPPING)")
    private String courierName;

    @Schema(description = "Updated delivery window")
    private LocalDateTime estimatedDeliveryFrom;

    private LocalDateTime estimatedDeliveryTo;

    @Size(max = 500)
    @Schema(description = "Note stored in the tracking history (e.g. cancel reason)")
    private String note;
}
