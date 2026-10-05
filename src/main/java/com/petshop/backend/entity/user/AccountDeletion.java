package com.petshop.backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * One deleted account: who it was (captured before anonymisation), why, and whether the user or an admin deleted it.
 * Lets admins review departures even though the user row itself no longer holds personal data.
 */
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

    /** Preset reason code chosen in the dashboard (e.g. NOT_USING); null when an admin deleted the account. */
    private String reason;

    /** Free-text explanation written by the user. */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /** SELF or ADMIN. */
    @Column(name = "deleted_by", nullable = false)
    private String deletedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
