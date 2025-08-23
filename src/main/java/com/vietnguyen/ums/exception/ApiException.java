package com.vietnguyen.ums.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception carrying an HTTP status and a machine-readable code
 * alongside a human-readable message.
 */
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
