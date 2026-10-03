package com.petshop.backend.entity.service;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {

    public static final int FEATURED_LIMIT = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String role;
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String note;

    private String avatar;

    /** Shown on the public provider page; at most 4 per provider. */
    private Boolean featured;
}
