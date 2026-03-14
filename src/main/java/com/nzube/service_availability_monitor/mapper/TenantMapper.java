package com.nzube.service_availability_monitor.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nzube.service_availability_monitor.dto.tenant.TenantMemberResponse;
import com.nzube.service_availability_monitor.dto.tenant.TenantResponse;
import com.nzube.service_availability_monitor.entity.Tenant;

@Component
public class TenantMapper {

    public TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getMembers().stream().map(
                        member -> new TenantMemberResponse(
                                member.getUser().getId(),
                                member.getUser().getName(),
                                member.getTenantRole()))
                        .toList());
    }

    public List<TenantResponse> toResponseList(List<Tenant> tenants) {
        return tenants.stream()
                .map(this::toResponse)
                .toList();
    }
}