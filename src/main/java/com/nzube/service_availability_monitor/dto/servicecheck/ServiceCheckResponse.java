package com.nzube.service_availability_monitor.dto.servicecheck;

import java.time.LocalDateTime;

import com.nzube.service_availability_monitor.enums.ServiceStatus;

public record ServiceCheckResponse(
    Long id,
    Long serviceId,
    ServiceStatus status,
    Integer statusCode,
    Long responseTimeMs,
    String errorMessage,
    LocalDateTime checkedAt
) {}