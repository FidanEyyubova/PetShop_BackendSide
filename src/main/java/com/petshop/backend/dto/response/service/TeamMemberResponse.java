package com.petshop.backend.dto.response.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberResponse {
    private Long id;
    private String fullName;
    private String role;
    private String experience;
    private String certifications;
    private String bio;
    private String note;
    private String avatar;
    private Boolean featured;
}
