package com.nzube.service_availability_monitor.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nzube.service_availability_monitor.dto.servicecheck.ServiceCheckResponse;
import com.nzube.service_availability_monitor.entity.ServiceCheck;

@Component
public class ServiceCheckMapper {

    public ServiceCheckResponse toResponse(ServiceCheck check) {
        return new ServiceCheckResponse(
                check.getId(),
                check.getService().getId(),
                check.getStatus(),
                check.getStatusCode(),
                check.getResponseTimeMs(),
                check.getErrorMessage(),
                check.getCheckedAt()
        );
    }

    public List<ServiceCheckResponse> toResponseList(List<ServiceCheck> checks) {
        return checks.stream()
                .map(this::toResponse)
                .toList();
    }
}
