package com.nzube.service_availability_monitor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nzube.service_availability_monitor.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
