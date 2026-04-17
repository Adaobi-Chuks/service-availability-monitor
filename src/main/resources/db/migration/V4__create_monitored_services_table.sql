CREATE TABLE monitored_services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    port INTEGER,
    service_type VARCHAR(50) NOT NULL CHECK (service_type IN ('HTTP', 'TCP')),
    current_status VARCHAR(50) NOT NULL DEFAULT 'UNKNOWN' CHECK (current_status IN ('UP', 'DOWN', 'UNKNOWN')),
    check_interval_seconds INTEGER NOT NULL,
    retry_count INTEGER NOT NULL,
    retry_interval_seconds INTEGER NOT NULL,
    last_checked_at TIMESTAMP WITH TIME ZONE,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    created_by BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_monitored_services_tenant_id ON monitored_services(tenant_id);
CREATE INDEX idx_monitored_services_created_by ON monitored_services(created_by);