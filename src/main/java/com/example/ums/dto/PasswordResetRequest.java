package com.example.ums.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String newPassword) {}
