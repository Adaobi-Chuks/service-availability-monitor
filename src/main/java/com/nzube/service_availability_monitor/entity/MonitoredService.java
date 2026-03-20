package com.nzube.service_availability_monitor.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nzube.service_availability_monitor.enums.ServiceStatus;
import com.nzube.service_availability_monitor.enums.ServiceType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "monitored_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoredService extends BaseEntity {

    @Column(nullable = false)
    @NonNull
    private String name;

    @Column(nullable = false)
    @NonNull
    private String url;

    @Column(nullable = true)
    private Integer port;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private ServiceStatus currentStatus;

    @Column(nullable = false)
    @NonNull
    private Integer checkIntervalSeconds;

    @Column(nullable = false)
    @NonNull
    private Integer retryCount;

    @Column(nullable = false)
    @NonNull
    private Integer retryIntervalSeconds;

    @Column(nullable = true)
    private LocalDateTime lastCheckedAt;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    @NonNull
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @NonNull
    private User createdBy;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServiceCheck> checks = new ArrayList<>();

}
