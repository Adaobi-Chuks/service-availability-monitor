package com.nzube.service_availability_monitor.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.nzube.service_availability_monitor.checker.HttpChecker;
import com.nzube.service_availability_monitor.checker.TcpChecker;
import com.nzube.service_availability_monitor.entity.MonitoredService;
import com.nzube.service_availability_monitor.entity.ServiceCheck;
import com.nzube.service_availability_monitor.enums.ServiceStatus;
import com.nzube.service_availability_monitor.enums.ServiceType;
import com.nzube.service_availability_monitor.notification.NotificationEvent;
import com.nzube.service_availability_monitor.notification.NotificationPublisher;
import com.nzube.service_availability_monitor.repository.MonitoredServiceRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceMonitorSchedulerService {

    private final MonitoredServiceRepository monitoredServiceRepository;
    private final HttpChecker httpChecker;
    private final TcpChecker tcpChecker;
    private final NotificationPublisher notificationPublisher;
    private final RedisService redisService;
    private final TaskScheduler taskScheduler;

    // tracks scheduled tasks so we can cancel/reschedule them
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void scheduleAllServices() {
        List<MonitoredService> services = monitoredServiceRepository.findAll();
        log.info("Scheduling {} services", services.size());
        services.forEach(this::scheduleService);
    }

    public void scheduleService(MonitoredService service) {
        // cancel existing task if already scheduled
        cancelService(service.getId());

        ScheduledFuture<?> task = taskScheduler.scheduleWithFixedDelay(
                () -> {
                    try {
                        checkService(service);
                    } catch (Exception e) {
                        log.error("Error checking service {}: {}", service.getName(), e.getMessage());
                    }
                },
                Duration.ofSeconds(service.getCheckIntervalSeconds()));

        scheduledTasks.put(service.getId(), task);
        log.info("Scheduled service {} with interval {}s", service.getName(), service.getCheckIntervalSeconds());
    }

    public void cancelService(Long serviceId) {
        ScheduledFuture<?> existingTask = scheduledTasks.get(serviceId);
        if (existingTask != null) {
            existingTask.cancel(false); // false = don't interrupt if running
            scheduledTasks.remove(serviceId);
        }
    }

    public void rescheduleService(MonitoredService service) {
        log.info("Rescheduling service {} with new interval {}s", service.getName(), service.getCheckIntervalSeconds());
        scheduleService(service);
    }

    private void checkService(MonitoredService service) {
        ServiceCheck result = performCheck(service);

        service.setLastCheckedAt(LocalDateTime.now());
        service.setCurrentStatus(result.getStatus());
        monitoredServiceRepository.save(service);

        ServiceStatus previousStatus = redisService.getLastKnownStatus(service.getId());
        redisService.saveLastKnownStatus(service.getId(), result.getStatus());

        boolean statusChanged = previousStatus != null && previousStatus != result.getStatus();
        boolean isRateLimited = redisService.isRateLimited(service.getId());

        if (statusChanged && !isRateLimited) {
            log.info("Status change detected for {}: {} → {}",
                    service.getName(), previousStatus, result.getStatus());
            publishNotification(service, result);
            redisService.setRateLimit(service.getId()); // set rate limit after notifying
        }
    }

    private ServiceCheck performCheck(MonitoredService service) {
        if (service.getServiceType() == ServiceType.HTTP) {
            return httpChecker.check(service);
        }
        return tcpChecker.check(service);
    }

    private void publishNotification(MonitoredService service, ServiceCheck result) {
        String ownerEmail = service.getTenant().getMembers().stream()
                .filter(ut -> ut.getTenantRole().name().equals("TENANT_OWNER"))
                .map(ut -> ut.getUser().getEmail())
                .findFirst()
                .orElse(null);

        if (ownerEmail == null) {
            log.warn("No owner found for tenant {}, skipping notification", service.getTenant().getId());
            return;
        }

        NotificationEvent event = new NotificationEvent(
                service.getId(),
                service.getName(),
                service.getUrl(),
                result.getStatus(),
                result.getResponseTimeMs(),
                result.getStatusCode(),
                result.getErrorMessage(),
                result.getCheckedAt(),
                ownerEmail);

        notificationPublisher.publishOutageEvent(event);
    }
}
