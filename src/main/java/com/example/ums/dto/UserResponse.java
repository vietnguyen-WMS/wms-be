package com.example.ums.dto;

import java.time.Instant;

public record UserResponse(Long id, String username, String status, Instant lastLoginAt, Instant createdAt, Instant updatedAt) {}
