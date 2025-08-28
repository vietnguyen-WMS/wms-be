package com.vietnguyen.ums.service;

import com.vietnguyen.ums.dto.LoginResponse;
import com.vietnguyen.ums.dto.UserInfo;
import com.vietnguyen.ums.entity.StatusCodeEntity;
import com.vietnguyen.ums.entity.UserEntity;
import com.vietnguyen.ums.entity.UserRoleEntity;
import com.vietnguyen.ums.entity.UserInfoEntity;
import com.vietnguyen.ums.repo.StatusCodeRepository;
import com.vietnguyen.ums.repo.UserRepository;
import com.vietnguyen.ums.repo.UserRoleRepository;
import com.vietnguyen.ums.repo.UserInfoRepository;
import com.vietnguyen.ums.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vietnguyen.ums.exception.ApiException;

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
    private final UserInfoRepository userInfoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, StatusCodeRepository statusCodeRepository,
                       UserRoleRepository userRoleRepository, UserInfoRepository userInfoRepository,
                       BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.statusCodeRepository = statusCodeRepository;
        this.userRoleRepository = userRoleRepository;
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(noRollbackFor = ApiException.class)
    public LoginResponse login(String username, String password) {
        UserEntity user = userRepository.findByUsernameIgnoreCase(username)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid credentials"));

        String statusCode = statusCodeRepository.findById(user.getStatusId())
                .map(StatusCodeEntity::getCode).orElse(null);
        String roleCode = userRoleRepository.findById(user.getRoleId())
                .map(UserRoleEntity::getCode).orElse(null);
        if ("inactive".equalsIgnoreCase(statusCode)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "USER_INACTIVE", "User inactive");
        }
        if ("locked".equalsIgnoreCase(statusCode)) {
            throw new ApiException(HttpStatus.LOCKED, "ACCOUNT_LOCKED", "Account locked");
        }

        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            user.setFailedLoginAttempts(0);
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
            String token = jwtUtil.generateToken(user);
            UserInfoEntity infoEntity = userInfoRepository.findById(user.getId()).orElse(null);
            UserInfo info = UserMapper.toInfo(user, infoEntity, statusCode, roleCode);
            log.info("Login success for {}", username);
            return new LoginResponse(token, info);
        } else {
            int attempts = Optional.ofNullable(user.getFailedLoginAttempts()).orElse(0) + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= LOCK_THRESHOLD) {
                Short lockedId = statusCodeRepository.findByDomainAndCode("ums", "locked")
                        .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "MISSING_STATUS_CODE", "Missing status code"))
                        .getId();
                user.setStatusId(lockedId);
                log.info("User {} locked after {} failed attempts", username, attempts);
            } else {
                log.info("Login failed for {} attempt {}", username, attempts);
            }
            userRepository.save(user);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid credentials");
        }
    }

    public UserInfo getUserInfo(Long userId) {
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        String statusCode = statusCodeRepository.findById(user.getStatusId())
                .map(StatusCodeEntity::getCode).orElse(null);
        String roleCode = userRoleRepository.findById(user.getRoleId())
                .map(UserRoleEntity::getCode).orElse(null);
        UserInfoEntity infoEntity = userInfoRepository.findById(user.getId()).orElse(null);
        return UserMapper.toInfo(user, infoEntity, statusCode, roleCode);
    }
}
