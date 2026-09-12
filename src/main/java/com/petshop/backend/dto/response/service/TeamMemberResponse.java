package com.petshop.backend.dto.response.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberResponse {
    private Long id;
    private String fullName;
    private String role;
    private String experience;
    private String note;
    private String avatar;
}
