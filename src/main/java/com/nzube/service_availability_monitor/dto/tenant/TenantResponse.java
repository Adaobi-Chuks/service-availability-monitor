package com.nzube.service_availability_monitor.dto.tenant;

import java.util.List;

public record TenantResponse(
    Long id,
    String name,
    List<TenantMemberResponse> members
) {}