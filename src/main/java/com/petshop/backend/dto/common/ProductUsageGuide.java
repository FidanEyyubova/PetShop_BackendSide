package com.petshop.backend.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUsageGuide {

    @Valid
    @Size(max = 30)
    @Builder.Default
    private List<PortionRow> portionTable = new ArrayList<>();

    @Size(max = 500)
    @Schema(example = "Twice a day")
    private String frequency;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortionRow {
        @Size(max = 50)
        @Schema(example = "5-10 kg")
        private String weight;

        @Size(max = 50)
        @Schema(example = "100-150 g")
        private String portion;
    }
}
