package com.petshop.backend.dto.response.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long userId;
    @Schema(description = "Author's name")
    private String userName;
    private String userAvatar;

    @Schema(description = "PRODUCT, STORE or SERVICE_PROVIDER")
    private String targetType;
    private Long targetId;
    @Schema(description = "Product title / store name / center name")
    private String targetName;
    @Schema(description = "Store of the product (product reviews)")
    private String storeName;

    private Integer rating;
    private String comment;
    private String petName;
    private String petPhoto;
    @Schema(description = "Author bought the product / ordered from the store")
    private Boolean verifiedPurchase;

    @Schema(description = "Reply from the vendor or service provider")
    private String reply;
    private LocalDateTime replyDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
