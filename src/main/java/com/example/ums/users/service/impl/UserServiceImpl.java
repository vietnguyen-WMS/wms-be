package com.example.ums.users.service.impl;

import com.example.ums.users.dto.LoginRequest;
import com.example.ums.users.dto.LoginResponse;
import com.example.ums.users.dto.UserResponse;
import com.example.ums.users.entity.User;
import com.example.ums.users.repository.UserRepository;
import com.example.ums.users.service.TokenService;
import com.example.ums.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("Attempting login for user: {}", loginRequest.getUsername());

        try {
            // Find user by username
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

            if (userOptional.isEmpty()) {
                logger.warn("Login failed: User not found - {}", loginRequest.getUsername());
                return new LoginResponse(false, "Invalid username or password");
            }

            User user = userOptional.get();
            
            // Debug logging
            logger.info("Found user: {}", user.getUsername());
            logger.info("Stored password hash: {}", user.getPassword());
            logger.info("Input password: {}", loginRequest.getPassword());
            
            // Check password using BCrypt
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            logger.info("Password matches: {}", passwordMatches);
            
            if (!passwordMatches) {
                logger.warn("Login failed: Invalid password for user - {}", loginRequest.getUsername());
                return new LoginResponse(false, "Invalid username or password");
            }

            // Generate and save token
            String token = tokenService.generateAndSaveToken(user);

            // Create user info
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getCreatedAt()
            );

            logger.info("Login successful for user: {}", loginRequest.getUsername());
            return new LoginResponse(true, "Login successful", token, userInfo);

        } catch (Exception e) {
            logger.error("Error during login for user: {}", loginRequest.getUsername(), e);
            return new LoginResponse(false, "An error occurred during login");
        }
    }

    @Override
    public UserResponse getCurrentUser(String tokenValue) {
        logger.info("Getting current user from token");

        try {
            UserResponse userResponse = tokenService.validateToken(tokenValue);
            
            if (userResponse != null) {
                logger.info("Current user retrieved successfully: {}", userResponse.getUsername());
            } else {
                logger.warn("Failed to get current user: invalid or expired token");
            }
            
            return userResponse;
        } catch (Exception e) {
            logger.error("Error getting current user from token", e);
            return null;
        }
    }
} 