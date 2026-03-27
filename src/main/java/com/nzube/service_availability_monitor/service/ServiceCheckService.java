package com.nzube.service_availability_monitor.service;

import java.util.List;
import java.util.OptionalDouble;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nzube.service_availability_monitor.dto.servicecheck.ServiceCheckStatsResponse;
import com.nzube.service_availability_monitor.entity.ServiceCheck;
import com.nzube.service_availability_monitor.enums.ServiceStatus;
import com.nzube.service_availability_monitor.exception.NotFoundException;
import com.nzube.service_availability_monitor.repository.ServiceCheckRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ServiceCheckService {

    private final ServiceCheckRepository serviceCheckRepository;

    public List<ServiceCheck> getChecksByService(Long serviceId) {
        return serviceCheckRepository.findByServiceIdOrderByCheckedAtDesc(serviceId);
    }

    public ServiceCheck getLatestCheck(Long serviceId) {
        return serviceCheckRepository.findTopByServiceIdOrderByCheckedAtDesc(serviceId)
                .orElseThrow(() -> new NotFoundException(serviceId, ServiceCheck.class));
    }

    public ServiceCheckStatsResponse getStats(Long serviceId) {
        List<ServiceCheck> checks = serviceCheckRepository.findByServiceIdOrderByCheckedAtDesc(serviceId);

        if (checks.isEmpty()) {
            return new ServiceCheckStatsResponse(serviceId, 0, 0, 0.0, 0L, 0L);
        }

        long totalChecks = checks.size();
        long upCount = checks.stream()
                .filter(c -> c.getStatus() == ServiceStatus.UP)
                .count();
        long downCount = totalChecks - upCount;
        double uptimePercentage = ((double) upCount / totalChecks) * 100;
        OptionalDouble avgResponseTime = checks.stream()
                .filter(c -> c.getResponseTimeMs() != null)
                .mapToLong(ServiceCheck::getResponseTimeMs)
                .average();

        return new ServiceCheckStatsResponse(
                serviceId,
                totalChecks,
                downCount,
                Math.round(uptimePercentage * 100.0) / 100.0,
                (long) avgResponseTime.orElse(0),
                checks.stream()
                        .filter(c -> c.getResponseTimeMs() != null)
                        .mapToLong(ServiceCheck::getResponseTimeMs)
                        .max()
                        .orElse(0)
        );
    }
}
