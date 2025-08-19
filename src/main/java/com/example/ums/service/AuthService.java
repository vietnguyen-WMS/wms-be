package com.example.ums.service;

import com.example.ums.dto.LoginResponse;
import com.example.ums.dto.UserInfo;
import com.example.ums.entity.StatusCodeEntity;
import com.example.ums.entity.UserEntity;
import com.example.ums.entity.UserRoleEntity;
import com.example.ums.repo.StatusCodeRepository;
import com.example.ums.repo.UserRepository;
import com.example.ums.repo.UserRoleRepository;
import com.example.ums.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    private static final int LOCK_THRESHOLD = 5;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final StatusCodeRepository statusCodeRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, StatusCodeRepository statusCodeRepository,
                       UserRoleRepository userRoleRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.statusCodeRepository = statusCodeRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public LoginResponse login(String username, String password) {
        UserEntity user = userRepository.findByUsernameIgnoreCase(username)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String statusCode = statusCodeRepository.findById(user.getStatusId())
                .map(StatusCodeEntity::getCode).orElse(null);
        String roleCode = userRoleRepository.findById(user.getRoleId())
                .map(UserRoleEntity::getCode).orElse(null);
        if ("inactive".equalsIgnoreCase(statusCode)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User inactive");
        }
        if ("locked".equalsIgnoreCase(statusCode)) {
            throw new ResponseStatusException(HttpStatus.LOCKED, "Account locked");
        }

        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            user.setFailedLoginAttempts(0);
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
            String token = jwtUtil.generateToken(user);
            UserInfo info = UserMapper.toInfo(user, statusCode, roleCode);
            log.info("Login success for {}", username);
            return new LoginResponse(token, info);
        } else {
            int attempts = Optional.ofNullable(user.getFailedLoginAttempts()).orElse(0) + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= LOCK_THRESHOLD) {
                Short lockedId = statusCodeRepository.findByDomainAndCode("ums", "locked")
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Missing status code"))
                        .getId();
                user.setStatusId(lockedId);
                log.info("User {} locked after {} failed attempts", username, attempts);
            } else {
                log.info("Login failed for {} attempt {}", username, attempts);
            }
            userRepository.save(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    public UserInfo getUserInfo(Long userId) {
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String statusCode = statusCodeRepository.findById(user.getStatusId())
                .map(StatusCodeEntity::getCode).orElse(null);
        String roleCode = userRoleRepository.findById(user.getRoleId())
                .map(UserRoleEntity::getCode).orElse(null);
        return UserMapper.toInfo(user, statusCode, roleCode);
    }
}
