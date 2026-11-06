package com.petshop.backend.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFacetsResponse {

    @Schema(description = "Products matching all filters")
    private long total;

    private List<CategoryFacet> categories;
    private List<PetTypeFacet> petTypes;
    private List<BrandFacet> brands;
    private PriceRange priceRange;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryFacet {
        private Long id;
        private String slug;
        private String name;
        private long count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PetTypeFacet {
        private String id;
        private String name;
        private String azName;
        private String ruName;
        private String label;
        private long count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BrandFacet {
        private String name;
        private long count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceRange {
        private BigDecimal min;
        private BigDecimal max;
    }
}
