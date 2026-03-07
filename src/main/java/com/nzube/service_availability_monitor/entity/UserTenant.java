package com.nzube.service_availability_monitor.entity;

import com.nzube.service_availability_monitor.enums.TenantRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "user_tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTenant extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private TenantRole tenantRole;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NonNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    @NonNull
    private Tenant tenant;
}