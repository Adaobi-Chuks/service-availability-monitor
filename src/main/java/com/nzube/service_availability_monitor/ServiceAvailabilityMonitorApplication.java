package com.nzube.service_availability_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.nzube.service_availability_monitor.config.property.JwtProperties;
import com.nzube.service_availability_monitor.config.property.RabbitMQProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, RabbitMQProperties.class, MailProperties.class})
@EnableScheduling
public class ServiceAvailabilityMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAvailabilityMonitorApplication.class, args);
	}

}
