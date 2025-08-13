package com.example.ums.users.service.impl;

import com.example.ums.users.dto.UserResponse;
import com.example.ums.users.entity.Token;
import com.example.ums.users.entity.User;
import com.example.ums.users.repository.TokenRepository;
import com.example.ums.users.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);
    
    // Token expiration time: 24 hours
    private static final int TOKEN_EXPIRATION_HOURS = 24;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public String generateAndSaveToken(User user) {
        logger.info("Generating new token for user: {}", user.getUsername());

        // Delete existing tokens for this user
        deleteTokensByUserId(user.getId());

        // Generate new token
        String tokenValue = "token_" + UUID.randomUUID().toString().replace("-", "");
        
        // Set expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS);

        // Create and save token
        Token token = new Token(tokenValue, user, expiresAt);
        tokenRepository.save(token);

        logger.info("Token generated successfully for user: {}", user.getUsername());
        return tokenValue;
    }

    @Override
    public UserResponse validateToken(String tokenValue) {
        if (tokenValue == null || tokenValue.trim().isEmpty()) {
            logger.warn("Token validation failed: token is null or empty");
            return null;
        }

        logger.debug("Validating token: {}", tokenValue);

        try {
            Optional<Token> tokenOptional = tokenRepository.findValidTokenByValue(tokenValue, LocalDateTime.now());

            if (tokenOptional.isEmpty()) {
                logger.warn("Token validation failed: token not found or expired");
                return null;
            }

            Token token = tokenOptional.get();
            User user = token.getUser();

            logger.info("Token validated successfully for user: {}", user.getUsername());

            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );

        } catch (Exception e) {
            logger.error("Error validating token: {}", tokenValue, e);
            return null;
        }
    }

    @Override
    public void deleteToken(String tokenValue) {
        if (tokenValue == null || tokenValue.trim().isEmpty()) {
            return;
        }

        logger.info("Deleting token: {}", tokenValue);

        try {
            Optional<Token> tokenOptional = tokenRepository.findByTokenValue(tokenValue);
            if (tokenOptional.isPresent()) {
                tokenRepository.delete(tokenOptional.get());
                logger.info("Token deleted successfully: {}", tokenValue);
            }
        } catch (Exception e) {
            logger.error("Error deleting token: {}", tokenValue, e);
        }
    }

    @Override
    public void deleteTokensByUserId(Long userId) {
        logger.info("Deleting all tokens for user ID: {}", userId);

        try {
            tokenRepository.deleteTokensByUserId(userId);
            logger.info("All tokens deleted successfully for user ID: {}", userId);
        } catch (Exception e) {
            logger.error("Error deleting tokens for user ID: {}", userId, e);
        }
    }

    @Override
    public void cleanupExpiredTokens() {
        logger.info("Starting cleanup of expired tokens");

        try {
            tokenRepository.deleteExpiredTokens(LocalDateTime.now());
            logger.info("Expired tokens cleanup completed");
        } catch (Exception e) {
            logger.error("Error during expired tokens cleanup", e);
        }
    }
} 