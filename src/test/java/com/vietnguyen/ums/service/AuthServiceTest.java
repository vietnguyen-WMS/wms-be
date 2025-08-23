package com.vietnguyen.ums.service;

import com.vietnguyen.ums.entity.StatusCodeEntity;
import com.vietnguyen.ums.entity.UserEntity;
import com.vietnguyen.ums.repo.StatusCodeRepository;
import com.vietnguyen.ums.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.vietnguyen.ums.exception.ApiException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusCodeRepository statusCodeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private StatusCodeEntity active;
    private StatusCodeEntity locked;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        statusCodeRepository.deleteAll();

        active = new StatusCodeEntity();
        active.setDomain("ums");
        active.setCode("active");
        active.setName("Active");
        active.setIsActive(true);
        active = statusCodeRepository.save(active);

        locked = new StatusCodeEntity();
        locked.setDomain("ums");
        locked.setCode("locked");
        locked.setName("Locked");
        locked.setIsActive(true);
        locked = statusCodeRepository.save(locked);

        UserEntity user = new UserEntity();
        user.setUsername("john");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setStatusId(active.getId());
        userRepository.save(user);
    }

    @Test
    void failedLoginsIncrementAndLockAccount() {
        for (int i = 1; i <= 5; i++) {
            try {
                authService.login("john", "wrong");
                fail("Expected ApiException");
            } catch (ApiException ex) {
                assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
            }
            UserEntity user = userRepository.findByUsernameIgnoreCase("john").orElseThrow();
            assertEquals(i, user.getFailedLoginAttempts());
            if (i < 5) {
                assertEquals(active.getId(), user.getStatusId());
            } else {
                assertEquals(locked.getId(), user.getStatusId());
            }
        }
    }
}

