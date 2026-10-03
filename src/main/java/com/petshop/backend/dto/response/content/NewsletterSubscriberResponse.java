package com.petshop.backend.dto.response.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSubscriberResponse {
    private Long id;
    private String email;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime unsubscribedAt;
}
