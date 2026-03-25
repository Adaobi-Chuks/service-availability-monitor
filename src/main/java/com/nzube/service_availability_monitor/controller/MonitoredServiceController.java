package com.nzube.service_availability_monitor.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nzube.service_availability_monitor.dto.monitoredService.CreateMonitoredServiceRequest;
import com.nzube.service_availability_monitor.dto.monitoredService.MonitoredServiceResponse;
import com.nzube.service_availability_monitor.dto.monitoredService.UpdateMonitoredServiceRequest;
import com.nzube.service_availability_monitor.entity.User;
import com.nzube.service_availability_monitor.mapper.MonitoredServiceMapper;
import com.nzube.service_availability_monitor.service.MonitoredServiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/services")
@Tag(name = "Monitored Services", description = "Manage services to monitor")
public class MonitoredServiceController {

    private final MonitoredServiceService monitoredServiceService;
    private final MonitoredServiceMapper monitoredServiceMapper;

    @PostMapping
    @Operation(summary = "Create a new monitored service")
    public ResponseEntity<MonitoredServiceResponse> createService(
            @Valid @RequestBody CreateMonitoredServiceRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(monitoredServiceMapper.toResponse(
                monitoredServiceService.createService(request, currentUser)));
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get all services for a tenant")
    public ResponseEntity<List<MonitoredServiceResponse>> getAllServicesByTenant(@PathVariable Long tenantId) {
        return ResponseEntity.ok(monitoredServiceMapper.toResponseList(
                monitoredServiceService.getAllServicesByTenant(tenantId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a monitored service by ID")
    public ResponseEntity<MonitoredServiceResponse> getService(@PathVariable Long id) {
        return ResponseEntity.ok(monitoredServiceMapper.toResponse(
                monitoredServiceService.getServiceById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a monitored service")
    public ResponseEntity<MonitoredServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMonitoredServiceRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(monitoredServiceMapper.toResponse(
                monitoredServiceService.updateService(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a monitored service")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        monitoredServiceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
