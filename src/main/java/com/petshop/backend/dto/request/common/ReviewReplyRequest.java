package com.petshop.backend.dto.request.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewReplyRequest {

    @NotBlank(message = "Reply is required")
    @Size(max = 2000)
    @JsonAlias("reply")
    @Schema(description = "Also accepted as `reply`", example = "Thank you for your feedback!")
    private String providerReply;
}
