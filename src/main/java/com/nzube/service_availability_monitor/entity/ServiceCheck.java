package com.nzube.service_availability_monitor.entity;

import java.time.LocalDateTime;

import com.nzube.service_availability_monitor.enums.ServiceStatus;

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
@Table(name = "service_checks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCheck extends BaseEntity {

    @ManyToOne
    @NonNull
    @JoinColumn(name = "service_id", nullable = false)
    private MonitoredService service;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private ServiceStatus status;

    @Column(nullable = true)
    private Integer statusCode;

    @Column(nullable = true)
    private Long responseTimeMs;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    @NonNull
    @Builder.Default
    private LocalDateTime checkedAt = LocalDateTime.now();
}
