package com.example.ums.users.service;

import com.example.ums.users.dto.LoginRequest;
import com.example.ums.users.dto.LoginResponse;

public interface UserService {

    /**
     * Authenticate user login
     * @param loginRequest the login credentials
     * @return LoginResponse containing authentication result
     */
    LoginResponse login(LoginRequest loginRequest);
} 