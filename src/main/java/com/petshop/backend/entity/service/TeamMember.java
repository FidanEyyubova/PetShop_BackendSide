package com.petshop.backend.entity.service;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String role;
    private String experience;
    private String note;
    private String avatar;
}
