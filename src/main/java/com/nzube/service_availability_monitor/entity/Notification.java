package com.nzube.service_availability_monitor.entity;

import java.time.LocalDateTime;

import com.nzube.service_availability_monitor.enums.NotificationStatus;
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
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    @NonNull
    private MonitoredService service;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NonNull
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private ServiceStatus status;

    @Column(nullable = false)
    @NonNull
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationStatus notificationStatus = NotificationStatus.PENDING;

    @Column(nullable = true)
    private LocalDateTime sentAt;
}
