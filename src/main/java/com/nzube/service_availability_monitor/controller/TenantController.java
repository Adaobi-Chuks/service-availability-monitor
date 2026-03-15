package com.nzube.service_availability_monitor.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nzube.service_availability_monitor.dto.tenant.CreateTenantRequest;
import com.nzube.service_availability_monitor.dto.tenant.TenantResponse;
import com.nzube.service_availability_monitor.entity.User;
import com.nzube.service_availability_monitor.mapper.TenantMapper;
import com.nzube.service_availability_monitor.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tenants")
@Tag(name = "Tenants", description = "Manage tenants")
public class TenantController {

    private final TenantService tenantService;
    private final TenantMapper tenantMapper;

    @PostMapping
    @Operation(summary = "Create a new tenant")
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(tenantMapper.toResponse(tenantService.createTenant(request, currentUser.getId())));
    }

    @GetMapping
    @Operation(summary = "Get all tenants")
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        return ResponseEntity.ok(tenantMapper.toResponseList(tenantService.getAllTenants()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a tenant by ID")
    public ResponseEntity<TenantResponse> getTenant(@PathVariable Long id) {
        return ResponseEntity.ok(tenantMapper.toResponse(tenantService.getTenantById(id)));
    }

}