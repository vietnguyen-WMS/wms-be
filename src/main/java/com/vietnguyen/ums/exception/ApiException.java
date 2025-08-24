package com.vietnguyen.ums.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception carrying an HTTP status and a machine-readable code
 * alongside a human-readable message.
 */
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String messageCode;

    public ApiException(HttpStatus status, String messageCode, String message) {
        super(message);
        this.status = status;
        this.messageCode = messageCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
