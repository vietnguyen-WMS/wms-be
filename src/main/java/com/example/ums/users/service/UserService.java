package com.example.ums.users.service;

import com.example.ums.users.dto.*;
import org.springframework.data.domain.Page;

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

    // CRUD operations
    Page<UserResponse> search(int page, int size, String q, boolean includeDeleted);

    UserResponse get(Long id);

    UserResponse create(CreateUserRequest req);

    UserResponse update(Long id, UpdateUserRequest req);

    void delete(Long id);
}