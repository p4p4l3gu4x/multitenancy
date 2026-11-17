package com.hudson.multitenancy.exception;

public class InvalidTenantNameException extends RuntimeException {
    public InvalidTenantNameException(final String message) {
        super(message);
    }
}
