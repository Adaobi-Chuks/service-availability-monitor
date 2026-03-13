package com.nzube.service_availability_monitor.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String field, String value, Class<?> entity) {
        super("The " + entity.getSimpleName().toLowerCase() + " with " + field + " '" + value + "' already exists in our records");
    }
}
