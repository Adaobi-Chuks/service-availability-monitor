package com.nzube.service_availability_monitor.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.nzube.service_availability_monitor.config.property.MailProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public void sendOutageEmail(String to, String serviceName, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(mailProperties.from());
            helper.setTo(to);
            helper.setSubject(buildSubject(serviceName, message));
            helper.setText(buildHtmlBody(serviceName, message), true);

            mailSender.send(mimeMessage);
            log.info("Email sent to {} for service {}", to, serviceName);
        } catch (MessagingException e) {
            log.error("Failed to send email to {} for service {}: {}", to, serviceName, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildSubject(String serviceName, String message) {
        if (message.contains("DOWN")) {
            return "🔴 Alert: " + serviceName + " is DOWN";
        }
        return "🟢 Resolved: " + serviceName + " is back UP";
    }

    private String buildHtmlBody(String serviceName, String message) {
        return """
                <html>
                    <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2 style="color: %s;">%s</h2>
                        <p>%s</p>
                        <hr/>
                        <p style="color: grey; font-size: 12px;">
                            This is an automated alert from Service Availability Monitor.
                        </p>
                    </body>
                </html>
                """.formatted(
                message.contains("DOWN") ? "#dc3545" : "#28a745",
                message.contains("DOWN") ? "🔴 Service Alert" : "🟢 Service Recovered",
                message);
    }
}
