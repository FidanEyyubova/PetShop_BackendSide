package com.petshop.backend.entity.service;

import com.petshop.backend.i18n.Translations;
import com.petshop.backend.i18n.TranslationsConverter;
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

    private Boolean featured;

    @Convert(converter = TranslationsConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private Translations translations = Translations.empty();
}
