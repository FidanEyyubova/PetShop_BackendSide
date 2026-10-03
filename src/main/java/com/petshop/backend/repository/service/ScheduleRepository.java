package com.petshop.backend.repository.service;

import com.petshop.backend.entity.service.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByProviderId(Long providerId);

    List<Schedule> findByProviderIdIn(Collection<Long> providerIds);

    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.providerId = :providerId")
    void deleteByProviderId(@Param("providerId") Long providerId);
}
