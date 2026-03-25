package com.nzube.service_availability_monitor.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nzube.service_availability_monitor.dto.monitoredService.MonitoredServiceResponse;
import com.nzube.service_availability_monitor.entity.MonitoredService;

@Component
public class MonitoredServiceMapper {

    public MonitoredServiceResponse toResponse(MonitoredService service) {
        return new MonitoredServiceResponse(
                service.getId(),
                service.getName(),
                service.getUrl(),
                service.getPort(),
                service.getServiceType(),
                service.getCurrentStatus(),
                service.getCheckIntervalSeconds(),
                service.getRetryCount(),
                service.getRetryIntervalSeconds(),
                service.getLastCheckedAt()
        );
    }

    public List<MonitoredServiceResponse> toResponseList(List<MonitoredService> services) {
        return services.stream()
                .map(this::toResponse)
                .toList();
    }
}
