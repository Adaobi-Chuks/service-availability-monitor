package com.nzube.service_availability_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.nzube.service_availability_monitor.config.property.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class})
@EnableScheduling
public class ServiceAvailabilityMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAvailabilityMonitorApplication.class, args);
	}

}
