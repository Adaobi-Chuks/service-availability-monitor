package com.nzube.service_availability_monitor.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nzube.service_availability_monitor.dto.monitoredService.CreateMonitoredServiceRequest;
import com.nzube.service_availability_monitor.dto.monitoredService.UpdateMonitoredServiceRequest;
import com.nzube.service_availability_monitor.entity.MonitoredService;
import com.nzube.service_availability_monitor.entity.Tenant;
import com.nzube.service_availability_monitor.entity.User;
import com.nzube.service_availability_monitor.enums.ServiceStatus;
import com.nzube.service_availability_monitor.exception.NotFoundException;
import com.nzube.service_availability_monitor.repository.MonitoredServiceRepository;
import com.nzube.service_availability_monitor.repository.TenantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MonitoredServiceService {

        private final MonitoredServiceRepository monitoredServiceRepository;
        private final TenantRepository tenantRepository;
        private final ServiceMonitorSchedulerService serviceMonitorSchedulerService;
        private final RedisService redisService;

        public MonitoredService createService(CreateMonitoredServiceRequest request, User currentUser) {

                Tenant tenant = tenantRepository.findById(request.tenantId())
                                .orElseThrow(() -> new NotFoundException(request.tenantId(), Tenant.class));

                MonitoredService service = MonitoredService.builder()
                                .name(request.name())
                                .url(request.url())
                                .port(request.port())
                                .serviceType(request.serviceType())
                                .checkIntervalSeconds(request.checkIntervalSeconds())
                                .retryCount(request.retryCount())
                                .retryIntervalSeconds(request.retryIntervalSeconds())
                                .currentStatus(ServiceStatus.UNKNOWN)
                                .tenant(tenant)
                                .createdBy(currentUser)
                                .build();

                monitoredServiceRepository.save(service);

                serviceMonitorSchedulerService.scheduleService(service); // schedule immediately after creation
                log.info("Created and scheduled service {} for tenant {}", service.getName(), tenant.getName());

                return service;
        }

        @Transactional(readOnly = true)
        public List<MonitoredService> getAllServicesByTenant(Long tenantId) {
                return monitoredServiceRepository.findByTenantId(tenantId);
        }

        @Transactional(readOnly = true)
        public MonitoredService getServiceById(Long id) {
                return monitoredServiceRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(id, MonitoredService.class));
        }

        public MonitoredService updateService(Long id, UpdateMonitoredServiceRequest request, User currentUser) {
                MonitoredService service = getServiceById(id);

                service.setName(request.name());
                service.setUrl(request.url());
                service.setPort(request.port());
                service.setCheckIntervalSeconds(request.checkIntervalSeconds());
                service.setRetryCount(request.retryCount());
                service.setRetryIntervalSeconds(request.retryIntervalSeconds());

                monitoredServiceRepository.save(service);
                serviceMonitorSchedulerService.rescheduleService(service); // reschedule with new interval
                log.info("Updated and rescheduled service {} by user {}", service.getName(), currentUser.getEmail());
                return service;
        }

        public void deleteService(Long id) {
                MonitoredService service = getServiceById(id);
                serviceMonitorSchedulerService.cancelService(id); // stop checking before deleting
                redisService.deleteLastKnownStatus(id); // clean up Redis
                monitoredServiceRepository.delete(service);
                log.info("Deleted service {}", service.getName());
        }
}