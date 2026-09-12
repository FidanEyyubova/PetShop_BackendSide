package com.petshop.backend.dto.request.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddReviewRequest {
    @NotBlank(message = "Hedef nÃ¶vÃ¼ boÅŸ ola bilmÉ™z (PRODUCT, SERVICE, STORE)")
    private String targetType;

    @NotNull(message = "Target ID mÃ¼tlÉ™qdir")
    private Long targetId;

    @NotNull(message = "Reyting mÃ¼tlÉ™qdir")
    @Min(value = 1, message = "Minimum 1 ulduz")
    @Max(value = 5, message = "Maksimum 5 ulduz")
    private Integer rating;

    private String comment;

    public AddReviewRequest() {
    }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}