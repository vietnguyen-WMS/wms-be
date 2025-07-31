package com.example.ums.users.controller;

import com.example.ums.users.dto.LoginRequest;
import com.example.ums.users.dto.LoginResponse;
import com.example.ums.users.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * POST endpoint for user login
     * @param loginRequest the login credentials
     * @return ResponseEntity containing login response
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Received login request for user: {}", loginRequest.getUsername());

        try {
            LoginResponse response = userService.login(loginRequest);

            if (response.isSuccess()) {
                logger.info("Login successful for user: {}", loginRequest.getUsername());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Login failed for user: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            logger.error("Error processing login request for user: {}", loginRequest.getUsername(), e);
            LoginResponse errorResponse = new LoginResponse(false, "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Debug endpoint to test BCrypt password verification
     * @return ResponseEntity with debug information
     */
    @GetMapping("/debug-password")
    public ResponseEntity<Map<String, Object>> debugPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "1";
        String hashFromDB = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        
        boolean matches = encoder.matches(password, hashFromDB);
        
        Map<String, Object> response = new HashMap<>();
        response.put("password", password);
        response.put("hashFromDB", hashFromDB);
        response.put("matches", matches);
        response.put("newHashForPassword1", encoder.encode("1"));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     * @return ResponseEntity with status message
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
} 