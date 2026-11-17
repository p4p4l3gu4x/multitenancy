package com.hudson.multitenancy.exception;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTenantNameException.class)
    public ResponseEntity<Map<String, String>> handleInvalidName(final InvalidTenantNameException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(final TenantNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({DuplicateTenantException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, String>> handleDuplicate(final Exception ex) {
        // DataIntegrityViolationException = unique constraint hit by a concurrent create
        return body(HttpStatus.CONFLICT, "Tenant already exists");
    }

    private static ResponseEntity<Map<String, String>> body(final HttpStatus status, final String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
