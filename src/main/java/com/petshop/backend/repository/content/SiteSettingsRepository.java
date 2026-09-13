package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.SiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteSettingsRepository extends JpaRepository<SiteSettings, Long> {
}