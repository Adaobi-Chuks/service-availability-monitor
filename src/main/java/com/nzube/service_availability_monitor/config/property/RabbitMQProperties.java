package com.nzube.service_availability_monitor.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMQProperties(
        Queue queue,
        Exchange exchange,
        RoutingKey routingKey
) {
    public record Queue(String notification) {}
    public record Exchange(String notification) {}
    public record RoutingKey(String notification) {}
}
