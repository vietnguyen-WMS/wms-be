package com.example.ums.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(@NotBlank String oldPassword,
                                    @NotBlank String newPassword) {}
