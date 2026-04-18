CREATE TABLE service_checks (
    id BIGSERIAL PRIMARY KEY,
    service_id BIGINT NOT NULL REFERENCES monitored_services(id),
    status VARCHAR(50) NOT NULL CHECK (status IN ('UP', 'DOWN', 'UNKNOWN')),
    status_code INTEGER,
    response_time_ms BIGINT,
    error_message TEXT,
    checked_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_service_checks_service_id ON service_checks(service_id);
CREATE INDEX idx_service_checks_checked_at ON service_checks(checked_at);