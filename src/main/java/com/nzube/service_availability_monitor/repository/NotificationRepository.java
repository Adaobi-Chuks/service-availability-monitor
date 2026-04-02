package com.nzube.service_availability_monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzube.service_availability_monitor.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {}
