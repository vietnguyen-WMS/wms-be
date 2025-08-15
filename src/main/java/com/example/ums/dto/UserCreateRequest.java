package com.example.ums.dto;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(@NotBlank String username, @NotBlank String password, String statusCode) {}
