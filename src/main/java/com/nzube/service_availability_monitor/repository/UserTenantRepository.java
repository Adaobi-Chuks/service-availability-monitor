package com.nzube.service_availability_monitor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzube.service_availability_monitor.entity.UserTenant;

public interface UserTenantRepository extends JpaRepository<UserTenant, Long> {
    List<UserTenant> findByUserId(Long userId);

    List<UserTenant> findByTenantId(Long tenantId);

    Optional<UserTenant> findByUserIdAndTenantId(Long userId, Long tenantId);
}
