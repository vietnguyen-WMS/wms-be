package com.vietnguyen.ums.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handle(ApiException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(ex.getMessageCode(), ex.getMessage(), req.getRequestURI(), Instant.now());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst();
        String msg = fieldError
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .orElse("Validation error");
        String messageCode = fieldError
                .map(f -> toCode(f.getField() + " " + f.getDefaultMessage()))
                .orElse("VALIDATION_ERROR");
        ErrorResponse body = new ErrorResponse(messageCode, msg, req.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private String toCode(String input) {
        return input.toUpperCase()
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_|_$", "");
    }
}
