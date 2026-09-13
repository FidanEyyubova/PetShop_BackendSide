package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.HeroSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeroSectionRepository extends JpaRepository<HeroSection, Long> {
    List<HeroSection> findByActiveTrue();
}