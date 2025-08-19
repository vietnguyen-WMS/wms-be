package com.example.ums.dto;

public record UserUpdateRequest(String username, String password, String statusCode, String roleCode) {}
