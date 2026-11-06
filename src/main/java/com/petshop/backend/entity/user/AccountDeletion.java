package com.petshop.backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_deletions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDeletion {

    public static final String BY_SELF = "SELF";
    public static final String BY_ADMIN = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String role;

    @Column(name = "full_name")
    private String fullName;

    private String email;

    private String reason;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "deleted_by", nullable = false)
    private String deletedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
