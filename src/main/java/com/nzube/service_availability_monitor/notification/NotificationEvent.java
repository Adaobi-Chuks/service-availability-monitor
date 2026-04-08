package com.nzube.service_availability_monitor.notification;

import java.time.LocalDateTime;

import com.nzube.service_availability_monitor.enums.ServiceStatus;

public record NotificationEvent(
        Long serviceId,
        String serviceName,
        String serviceUrl,
        ServiceStatus status,
        Long responseTimeMs,
        Integer statusCode,
        String errorMessage,
        LocalDateTime checkedAt,
        String tenantOwnerEmail
) {}
