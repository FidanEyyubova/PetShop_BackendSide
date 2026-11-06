package com.petshop.backend.entity.user;

import com.petshop.backend.common.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String password;

    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "city_address")
    private String cityAddress;

    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "notify_orders")
    @Builder.Default
    private Boolean notifyOrders = true;

    @Column(name = "notify_promotions")
    @Builder.Default
    private Boolean notifyPromotions = true;

    @Column(name = "notify_blog")
    @Builder.Default
    private Boolean notifyBlog = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = UserStatus.ACTIVE;
        if (email != null) email = email.trim().toLowerCase();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
