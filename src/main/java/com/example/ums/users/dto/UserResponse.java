package com.example.ums.users.dto;

import java.time.Instant;

public class UserResponse {

    private Long id;
    private String username;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    // Default constructor
    public UserResponse() {}

    // Constructor with parameters
    public UserResponse(Long id, String username, String status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
} 