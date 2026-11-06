package com.petshop.backend.dto.response.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategoryResponse {
    @Schema(example = "grooming")
    private String id;
    @Schema(example = "GROOMING")
    private String type;
    @Schema(example = "Grooming")
    private String name;
    @Schema(example = "Qulluq")
    private String azName;
    @Schema(example = "Груминг")
    private String ruName;
    @Schema(description = "Name in the request language (?lang= or Accept-Language)", example = "Qulluq")
    private String label;
    @Schema(description = "Approved providers in this category")
    private long providerCount;
}
