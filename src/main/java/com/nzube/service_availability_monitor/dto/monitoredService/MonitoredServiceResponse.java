package com.nzube.service_availability_monitor.dto.monitoredService;

import java.time.LocalDateTime;

import com.nzube.service_availability_monitor.enums.ServiceStatus;
import com.nzube.service_availability_monitor.enums.ServiceType;

public record MonitoredServiceResponse(
    Long id,
    String name,
    String url,
    Integer port,
    ServiceType serviceType,
    ServiceStatus currentStatus,
    Integer checkIntervalSeconds,
    Integer retryCount,
    Integer retryIntervalSeconds,
    LocalDateTime lastCheckedAt
) {}
