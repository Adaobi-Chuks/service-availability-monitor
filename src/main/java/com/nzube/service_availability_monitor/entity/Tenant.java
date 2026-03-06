package com.nzube.service_availability_monitor.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity {

    @Column(nullable = false)
    @NonNull
    private String name;

    @OneToMany(mappedBy = "tenant")
    @Builder.Default
    private List<UserTenant> members = new ArrayList<>();

    @OneToMany(mappedBy = "tenant")
    @Builder.Default
    private List<MonitoredService> services = new ArrayList<>();
}
