package com.nzube.service_availability_monitor.controller;

import com.nzube.service_availability_monitor.dto.servicecheck.ServiceCheckResponse;
import com.nzube.service_availability_monitor.dto.servicecheck.ServiceCheckStatsResponse;
import com.nzube.service_availability_monitor.mapper.ServiceCheckMapper;
import com.nzube.service_availability_monitor.service.ServiceCheckService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/checks")
@Tag(name = "Service Checks", description = "Manage service checks")
public class ServiceCheckController {

        private final ServiceCheckService serviceCheckService;
        private final ServiceCheckMapper serviceCheckMapper;

        @GetMapping("/service/{serviceId}")
        @Operation(summary = "Get all checks for a service")
        public ResponseEntity<List<ServiceCheckResponse>> getChecksByService(
                @PathVariable Long serviceId
        ) {
                return ResponseEntity.ok(serviceCheckMapper.toResponseList(
                        serviceCheckService.getChecksByService(serviceId)
                ));
        }

        @GetMapping("/service/{serviceId}/latest")
        @Operation(summary = "Get the latest check for a service")
        public ResponseEntity<ServiceCheckResponse> getLatestCheck(
                @PathVariable Long serviceId
        ) {
                return ResponseEntity.ok(serviceCheckMapper.toResponse(
                        serviceCheckService.getLatestCheck(serviceId)
                ));
        }

        @GetMapping("/service/{serviceId}/stats")
        @Operation(summary = "Get availability stats for a service")
        public ResponseEntity<ServiceCheckStatsResponse> getStats(
                @PathVariable Long serviceId
        ) {
                return ResponseEntity.ok(serviceCheckService.getStats(serviceId));
        }
}
