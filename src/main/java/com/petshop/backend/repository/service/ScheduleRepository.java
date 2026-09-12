package com.petshop.backend.repository.service;

import com.petshop.backend.entity.service.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByProviderId(Long providerId);
}
