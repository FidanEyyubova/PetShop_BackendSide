package com.petshop.backend.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pet category shown on the home page (click -> /shop?pet={id})")
public class PetTypeResponse {
    @Schema(example = "dogs")
    private String id;
    @Schema(example = "Dogs")
    private String name;
    @Schema(example = "İtlər")
    private String azName;
    @Schema(example = "Собаки")
    private String ruName;
    @Schema(description = "Name in the request language (?lang= or Accept-Language)", example = "İtlər")
    private String label;
    @Schema(description = "Icon hint for the frontend", example = "dog")
    private String iconName;
    @Schema(description = "Active products for this pet type")
    private long productCount;
}
