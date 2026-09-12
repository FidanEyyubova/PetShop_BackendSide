package com.petshop.backend.repository.service;

import com.petshop.backend.entity.service.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByProviderId(Long providerId);
}
