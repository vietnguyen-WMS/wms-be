package com.example.ums.users.service;

import com.example.ums.users.dto.UserResponse;
import com.example.ums.users.entity.User;

public interface TokenService {

    /**
     * Generate and save a new token for a user
     * @param user the user to generate token for
     * @return the generated token value
     */
    String generateAndSaveToken(User user);

    /**
     * Validate a token and return the associated user
     * @param tokenValue the token to validate
     * @return UserResponse if token is valid, null otherwise
     */
    UserResponse validateToken(String tokenValue);

    /**
     * Delete a specific token
     * @param tokenValue the token to delete
     */
    void deleteToken(String tokenValue);

    /**
     * Delete all tokens for a user
     * @param userId the user ID
     */
    void deleteTokensByUserId(Long userId);

    /**
     * Clean up expired tokens
     */
    void cleanupExpiredTokens();
} 