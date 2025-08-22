package com.vietnguyen.ums.dto;

import java.time.Instant;

public record UserResponse(Long id, String username, String status, String role,
                           Instant lastLoginAt, Instant createdAt, Instant updatedAt) {
}
