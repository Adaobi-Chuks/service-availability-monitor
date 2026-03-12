package com.nzube.service_availability_monitor.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nzube.service_availability_monitor.dto.tenant.CreateTenantRequest;
import com.nzube.service_availability_monitor.entity.Tenant;
import com.nzube.service_availability_monitor.entity.User;
import com.nzube.service_availability_monitor.entity.UserTenant;
import com.nzube.service_availability_monitor.enums.TenantRole;
import com.nzube.service_availability_monitor.exception.NotFoundException;
import com.nzube.service_availability_monitor.repository.TenantRepository;
import com.nzube.service_availability_monitor.repository.UserRepository;
import com.nzube.service_availability_monitor.repository.UserTenantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final UserTenantRepository userTenantRepository;

    public Tenant createTenant(CreateTenantRequest request, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userId, User.class));

        Tenant tenant = Tenant.builder()
                .name(request.name())
                .build();
        tenantRepository.save(tenant);

        UserTenant userTenant = UserTenant.builder()
                .user(owner)
                .tenant(tenant)
                .tenantRole(TenantRole.TENANT_OWNER)
                .build();
        userTenantRepository.save(userTenant);

        return tenant;
    }

    @Transactional(readOnly = true)
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, Tenant.class));
    }
}