package com.example.ums.exception;

import java.time.Instant;

public record ErrorResponse(String error, String message, String path, Instant timestamp) {}
