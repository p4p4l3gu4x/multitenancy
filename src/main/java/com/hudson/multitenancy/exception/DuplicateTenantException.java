package com.hudson.multitenancy.exception;

public class DuplicateTenantException extends RuntimeException {
    public DuplicateTenantException(final String message) {
        super(message);
    }
}
