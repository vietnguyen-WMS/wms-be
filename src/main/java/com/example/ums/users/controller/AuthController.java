package com.example.ums.users.controller;

import com.example.ums.users.dto.LoginRequest;
import com.example.ums.users.dto.LoginResponse;
import com.example.ums.users.dto.UserResponse;
import com.example.ums.users.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * POST endpoint for user login
     * @param loginRequest the login credentials
     * @param response HTTP response for setting cookies
     * @return ResponseEntity containing login response
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, 
                                             HttpServletResponse response) {
        logger.info("Received login request for user: {}", loginRequest.getUsername());

        try {
            LoginResponse loginResponse = userService.login(loginRequest);

            if (loginResponse.isSuccess()) {
                // Set authentication token as HTTP-only cookie
                Cookie authCookie = new Cookie("auth_token", loginResponse.getToken());
                authCookie.setHttpOnly(true);
                authCookie.setSecure(false); // Set to true in production with HTTPS
                authCookie.setPath("/");
                authCookie.setMaxAge(24 * 60 * 60); // 24 hours in seconds
                response.addCookie(authCookie);

                logger.info("Login successful for user: {} - token set in cookie", loginRequest.getUsername());
                return ResponseEntity.ok(loginResponse);
            } else {
                logger.warn("Login failed for user: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
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
     * GET endpoint to get current user information from token
     * @param request HTTP request to extract token from cookie
     * @return ResponseEntity containing user information
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest request) {
        logger.info("Received request to get current user");

        try {
            // Extract token from cookie
            String token = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("auth_token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            if (token == null) {
                logger.warn("No authentication token found in cookies");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            UserResponse userResponse = userService.getCurrentUser(token);

            if (userResponse != null) {
                logger.info("Current user retrieved successfully: {}", userResponse.getUsername());
                return ResponseEntity.ok(userResponse);
            } else {
                logger.warn("Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST endpoint to logout user (clear token)
     * @param response HTTP response for clearing cookies
     * @return ResponseEntity with logout status
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, 
                                                     HttpServletResponse response) {
        logger.info("Received logout request");

        try {
            // Extract token from cookie
            String token = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("auth_token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            // Clear the authentication cookie
            Cookie authCookie = new Cookie("auth_token", "");
            authCookie.setHttpOnly(true);
            authCookie.setSecure(false);
            authCookie.setPath("/");
            authCookie.setMaxAge(0); // Delete the cookie
            response.addCookie(authCookie);

            // TODO: Invalidate token in database (optional)
            // tokenService.deleteToken(token);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Logout successful");

            logger.info("Logout successful");
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            logger.error("Error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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