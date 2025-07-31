package com.example.ums.users.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        // Test the existing hash from your database
        String existingHash = "$2a$10$eBv9DdHqXPkU5U/5zW6B8.7kW5Z7nZdLxqF7qWyYOXtT0JhLdTuQu";
        
        System.out.println("=== BCrypt Password Verification ===");
        System.out.println("Existing hash: " + existingHash);
        System.out.println();
        
        // Test common passwords
        String[] testPasswords = {"1", "password", "admin", "123", "123456", "admin123", "password123"};
        
        for (String password : testPasswords) {
            boolean matches = encoder.matches(password, existingHash);
            System.out.println("Password '" + password + "' matches: " + matches);
        }
        
        System.out.println();
        System.out.println("=== Generate New Hash for '1' ===");
        String newHash = encoder.encode("1");
        System.out.println("New hash for '1': " + newHash);
        System.out.println("Verification: " + encoder.matches("1", newHash));
        
        System.out.println();
        System.out.println("=== Generate New Hash for 'admin' ===");
        String adminHash = encoder.encode("admin");
        System.out.println("New hash for 'admin': " + adminHash);
        System.out.println("Verification: " + encoder.matches("admin", adminHash));
    }
} 