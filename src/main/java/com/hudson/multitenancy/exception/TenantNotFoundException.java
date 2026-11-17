package com.hudson.multitenancy.exception;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(final String message) {
        super(message);
    }
}
