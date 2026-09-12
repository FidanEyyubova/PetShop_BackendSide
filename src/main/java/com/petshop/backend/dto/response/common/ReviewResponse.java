package com.petshop.backend.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userAvatar;
    private String targetType;
    private Long targetId;
    private Integer rating;
    private String comment;
    private String providerReply;
    private LocalDateTime createdAt;
}
