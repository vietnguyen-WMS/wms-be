package com.example.ums.users.dto;

import java.time.LocalDateTime;

public class LoginResponse {

    private boolean success;
    private String message;
    private String token;
    private UserInfo userInfo;
    private LocalDateTime timestamp;

    // Default constructor
    public LoginResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for success response
    public LoginResponse(boolean success, String message, String token, UserInfo userInfo) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.userInfo = userInfo;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for error response
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Static inner class for user information
    public static class UserInfo {
        private Long id;
        private String username;
        private LocalDateTime createdAt;

        public UserInfo() {}

        public UserInfo(Long id, String username, LocalDateTime createdAt) {
            this.id = id;
            this.username = username;
            this.createdAt = createdAt;
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

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
} 