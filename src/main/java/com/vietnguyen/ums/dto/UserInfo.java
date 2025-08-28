package com.vietnguyen.ums.dto;

import java.time.Instant;

public record UserInfo(Long id, String username, String status, String role, Instant lastLoginAt,
                       String displayName, String avatarUrl, String bio, String address) {}
