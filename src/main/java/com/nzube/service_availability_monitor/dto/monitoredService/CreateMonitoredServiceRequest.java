package com.nzube.service_availability_monitor.dto.monitoredService;

import com.nzube.service_availability_monitor.enums.ServiceType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMonitoredServiceRequest(
    @NotBlank String name,
    @NotBlank String url,
    Integer port,
    @NotNull ServiceType serviceType,
    @NotNull @Min(30) Integer checkIntervalSeconds,
    @NotNull @Min(1) Integer retryCount,
    @NotNull @Min(5) Integer retryIntervalSeconds,
    @NotNull Long tenantId
) {}