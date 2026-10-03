package com.petshop.backend.repository.service;

import com.petshop.backend.entity.service.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByProviderIdOrderByIdAsc(Long providerId);

    Optional<TeamMember> findByIdAndProviderId(Long id, Long providerId);

    long countByProviderIdAndFeaturedTrue(Long providerId);
}
