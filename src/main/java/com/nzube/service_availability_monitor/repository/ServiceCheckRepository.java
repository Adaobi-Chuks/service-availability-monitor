package com.nzube.service_availability_monitor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzube.service_availability_monitor.entity.ServiceCheck;

public interface ServiceCheckRepository extends JpaRepository<ServiceCheck, Long> {
    List<ServiceCheck> findByServiceIdOrderByCheckedAtDesc(Long serviceId);
    Optional<ServiceCheck> findTopByServiceIdOrderByCheckedAtDesc(Long serviceId);
}
