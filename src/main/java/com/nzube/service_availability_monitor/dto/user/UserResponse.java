package com.nzube.service_availability_monitor.dto.user;

import com.nzube.service_availability_monitor.enums.PlatformRole;

public record UserResponse(
    Long id,
    String name,
    String email,
    PlatformRole platformRole
) {}
