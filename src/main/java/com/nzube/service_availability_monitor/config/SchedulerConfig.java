package com.nzube.service_availability_monitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // 10 threads running checks concurrently
        scheduler.setThreadNamePrefix("service-monitor-");
        scheduler.setErrorHandler(throwable -> System.err.println("Error in scheduled task: " + throwable.getMessage()));
        scheduler.initialize();
        return scheduler;
    }
}
