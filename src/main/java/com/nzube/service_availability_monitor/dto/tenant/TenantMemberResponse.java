package com.nzube.service_availability_monitor.dto.tenant;

import com.nzube.service_availability_monitor.enums.TenantRole;

public record TenantMemberResponse(
    Long userId,
    String userName,
    TenantRole tenantRole
) {}