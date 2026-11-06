package com.petshop.backend.dto.request.content;

import com.petshop.backend.i18n.TranslatableRequest;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlogSubCategoryRequest implements TranslatableRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100)
    @Schema(example = "Nutrition")
    private String title;

    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug may contain lower-case letters, digits and dashes")
    @Size(max = 100)
    @Schema(description = "Optional; generated from the title", example = "nutrition")
    private String slug;

    @Schema(description = TRANSLATIONS_DOC + " Fields: title")
    private Map<String, Map<String, Object>> translations;
}
