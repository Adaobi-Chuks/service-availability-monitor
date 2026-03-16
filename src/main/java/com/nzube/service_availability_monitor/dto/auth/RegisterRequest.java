package com.nzube.service_availability_monitor.dto.auth;

import com.nzube.service_availability_monitor.enums.PlatformRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    @NotBlank String password,
    PlatformRole platformRole
) {}
