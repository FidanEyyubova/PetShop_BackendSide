package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.AboutSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutSectionRepository extends JpaRepository<AboutSection, Long> {
}