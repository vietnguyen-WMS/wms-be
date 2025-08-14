package com.example.ums.users.service.impl;

import com.example.ums.users.dto.*;
import com.example.ums.users.entity.User;
import com.example.ums.users.repository.UserRepository;
import com.example.ums.users.service.TokenService;
import com.example.ums.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    @Qualifier("usersPasswordEncoder")
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("Attempting login for user: {}", loginRequest.getUsername());

        try {
            // Find user by username
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

            if (userOptional.isEmpty() || "DELETED".equals(userOptional.get().getStatus())) {
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
                    LocalDateTime.ofInstant(user.getCreatedAt(), ZoneOffset.UTC)
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

    // ================= CRUD operations =================

    @Override
    public Page<UserResponse> search(int page, int size, String q, boolean includeDeleted) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.search(q, includeDeleted, pageable)
                .map(this::toResponse);
    }

    @Override
    public UserResponse get(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> !"DELETED".equals(u.getStatus()))
                .orElseThrow(() -> new com.example.ums.users.exception.UserNotFoundException("User not found"));
        return toResponse(user);
    }

    @Override
    public UserResponse create(CreateUserRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new com.example.ums.users.exception.UsernameAlreadyExistsException("Username already exists");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setStatus("ACTIVE");
        user = userRepository.save(user);
        return toResponse(user);
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id)
                .filter(u -> !"DELETED".equals(u.getStatus()))
                .orElseThrow(() -> new com.example.ums.users.exception.UserNotFoundException("User not found"));

        if (req.getUsername() != null && !req.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(req.getUsername())) {
                throw new com.example.ums.users.exception.UsernameAlreadyExistsException("Username already exists");
            }
            user.setUsername(req.getUsername());
        }

        if (req.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }

        user = userRepository.save(user);
        return toResponse(user);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> !"DELETED".equals(u.getStatus()))
                .orElseThrow(() -> new com.example.ums.users.exception.UserNotFoundException("User not found"));
        user.setStatus("DELETED");
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}