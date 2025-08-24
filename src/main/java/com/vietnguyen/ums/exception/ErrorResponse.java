package com.vietnguyen.ums.exception;

import java.time.Instant;

public record ErrorResponse(String messageCode, String message, String path, Instant timestamp) {}
