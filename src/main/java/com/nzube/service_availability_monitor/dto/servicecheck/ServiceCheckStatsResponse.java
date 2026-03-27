package com.nzube.service_availability_monitor.dto.servicecheck;

public record ServiceCheckStatsResponse(
    Long serviceId,
    long totalChecks,
    long downCount,
    double uptimePercentage,
    long avgResponseTimeMs,
    long maxResponseTimeMs
) {}
