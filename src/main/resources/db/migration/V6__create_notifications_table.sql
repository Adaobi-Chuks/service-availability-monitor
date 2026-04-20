CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    service_id BIGINT NOT NULL REFERENCES monitored_services(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL CHECK (status IN ('UP', 'DOWN', 'UNKNOWN')),
    message TEXT NOT NULL,
    notification_status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (notification_status IN ('PENDING', 'SENT', 'FAILED')),
    sent_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_notifications_service_id ON notifications(service_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);