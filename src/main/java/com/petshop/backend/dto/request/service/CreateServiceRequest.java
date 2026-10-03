package com.petshop.backend.dto.request.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/** One line of the provider's price list. */
@Data
public class CreateServiceRequest {

    @NotBlank(message = "Service name is required")
    @Size(max = 200)
    @JsonAlias("name")
    @Schema(example = "Full grooming")
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Minimum price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    @JsonAlias({"basePrice", "price"})
    @Schema(description = "Lowest price (also accepted as basePrice)", example = "35.00")
    private BigDecimal priceMin;

    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    @Schema(description = "Highest price; must be >= priceMin; omit for a fixed price", example = "50.00")
    private BigDecimal priceMax;

    @Size(max = 100)
    @JsonAlias("duration")
    @Schema(description = "Free-text duration as shown to customers", example = "45-60 min")
    private String durationText;

    @Min(1) @Max(1440)
    private Integer durationMinutes;

    private Boolean active;

    private Boolean emergencyService;

    private Long categoryId;
}
