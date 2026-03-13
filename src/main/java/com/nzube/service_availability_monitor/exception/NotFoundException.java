package com.nzube.service_availability_monitor.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Long id, Class<?> entity) {
        super("The " + entity.getSimpleName().toLowerCase() + " with id '" + id + "' does not exist in our records");
    }

    public NotFoundException(String field, String value, Class<?> entity) {
        super("The " + entity.getSimpleName().toLowerCase() + " with " + field + " '" + value + "' does not exist in our records");
    }
}