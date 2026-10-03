package com.petshop.backend.entity.content;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Blog category (health, nutrition, grooming ...). Table name kept for compatibility. */
@Entity
@Table(name = "blog_sub_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogSubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String slug;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
