package com.nzube.service_availability_monitor.notification;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.nzube.service_availability_monitor.config.property.RabbitMQProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    public void publishOutageEvent(NotificationEvent event) {
        rabbitTemplate.convertAndSend(
                properties.exchange().notification(),
                properties.routingKey().notification(),
                event
        );
        log.info("Published outage event for service {} → {}", event.serviceName(), event.status());
    }
}
