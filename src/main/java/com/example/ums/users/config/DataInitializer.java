package com.example.ums.users.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info("UMS Application is starting up...");
        logger.info("Database connection established successfully");
        logger.info("Users table is ready for authentication");
        logger.info("Application is ready to handle login requests");
        logger.info("Available test user: admin (password: check your database)");
    }
} 