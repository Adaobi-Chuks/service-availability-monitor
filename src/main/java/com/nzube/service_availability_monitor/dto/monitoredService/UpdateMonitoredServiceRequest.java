package com.nzube.service_availability_monitor.dto.monitoredService;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateMonitoredServiceRequest(
    @NotBlank String name,
    @NotBlank String url,
    Integer port,
    @NotNull @Min(30) Integer checkIntervalSeconds,
    @NotNull @Min(1) Integer retryCount,
    @NotNull @Min(5) Integer retryIntervalSeconds
) {}
