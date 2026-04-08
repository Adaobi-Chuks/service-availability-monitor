package com.nzube.service_availability_monitor.notification;

import com.nzube.service_availability_monitor.entity.Notification;
import com.nzube.service_availability_monitor.enums.NotificationStatus;
import com.nzube.service_availability_monitor.repository.NotificationRepository;
import com.nzube.service_availability_monitor.repository.MonitoredServiceRepository;
import com.nzube.service_availability_monitor.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final MonitoredServiceRepository monitoredServiceRepository;

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void consume(NotificationEvent event) {
        log.info("Received notification event for service {} → {}", event.serviceName(), event.status());

        Notification notification = Notification.builder()
                .service(monitoredServiceRepository.getReferenceById(event.serviceId()))
                .message(buildMessage(event))
                .status(event.status())
                .notificationStatus(NotificationStatus.PENDING)
                .build();

        notificationRepository.save(notification);

        try {
            emailService.sendOutageEmail(
                    event.tenantOwnerEmail(),
                    event.serviceName(),
                    buildMessage(event));
            notification.setNotificationStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Notification sent successfully for service {}", event.serviceName());
        } catch (Exception e) {
            notification.setNotificationStatus(NotificationStatus.FAILED);
            log.error("Failed to send notification for service {}: {}", event.serviceName(), e.getMessage());
        } finally {
            notificationRepository.save(notification);
        }
    }

    private String buildMessage(NotificationEvent event) {
        if (event.status().name().equals("DOWN")) {
            return String.format(
                    "Service '%s' at %s is DOWN. %s. Response time: %dms. Detected at: %s",
                    event.serviceName(),
                    event.serviceUrl(),
                    event.errorMessage() != null ? event.errorMessage() : "Status code: " + event.statusCode(),
                    event.responseTimeMs(),
                    event.checkedAt());
        }
        return String.format(
                "Service '%s' at %s is back UP. Response time: %dms. Detected at: %s",
                event.serviceName(),
                event.serviceUrl(),
                event.responseTimeMs(),
                event.checkedAt());
    }
}
