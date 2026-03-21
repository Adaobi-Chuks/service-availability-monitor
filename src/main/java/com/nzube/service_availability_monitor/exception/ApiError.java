package com.nzube.service_availability_monitor.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public record ApiError(int status, String error, String message, LocalDateTime timestamp) {
    public static ApiError of(HttpStatus status, String message) {
        return new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now());
    }
}
