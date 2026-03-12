package com.nzube.service_availability_monitor.dto.tenant;

import jakarta.validation.constraints.NotBlank;

public record CreateTenantRequest(
    @NotBlank String name
) {}