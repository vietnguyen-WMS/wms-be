package com.example.ums.users.service;

import com.example.ums.users.dto.LoginRequest;
import com.example.ums.users.dto.LoginResponse;
import com.example.ums.users.dto.UserResponse;

public interface UserService {

    /**
     * Authenticate user login
     * @param loginRequest the login credentials
     * @return LoginResponse containing authentication result
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * Get current user information from token
     * @param tokenValue the authentication token
     * @return UserResponse containing user information
     */
    UserResponse getCurrentUser(String tokenValue);
} 