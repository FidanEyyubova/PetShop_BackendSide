package com.petshop.backend.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelValue {
    @Size(max = 100)
    @Schema(example = "Protein")
    private String label;

    @Size(max = 100)
    @Schema(example = "26%")
    private String value;
}
