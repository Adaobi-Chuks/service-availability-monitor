package com.nzube.service_availability_monitor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzube.service_availability_monitor.entity.MonitoredService;

public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {
    List<MonitoredService> findByTenantId(Long tenantId);
}
