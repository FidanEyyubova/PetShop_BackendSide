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
public class ProductIngredients {

    @Schema(example = "[\"Grain\", \"Soy\", \"Artificial colours\"]")
    @Size(max = 30)
    @Builder.Default
    private List<@Size(max = 100) String> freeFrom = new ArrayList<>();

    @Valid
    @Size(max = 50)
    @Builder.Default
    private List<LabelValue> nutritionValues = new ArrayList<>();

    @Size(max = 2000)
    @Schema(example = "Chicken (40%), rice, salmon oil")
    private String activeIngredients;
}
