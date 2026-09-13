package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.AdminServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminServiceRepository extends JpaRepository<AdminServiceEntity, Long> {
    List<AdminServiceEntity> findByActiveTrue();
}