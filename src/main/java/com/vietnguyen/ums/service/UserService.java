package com.vietnguyen.ums.service;

import com.vietnguyen.ums.dto.*;
import com.vietnguyen.ums.entity.StatusCodeEntity;
import com.vietnguyen.ums.entity.UserEntity;
import com.vietnguyen.ums.entity.UserRoleEntity;
import com.vietnguyen.ums.repo.StatusCodeRepository;
import com.vietnguyen.ums.repo.UserRepository;
import com.vietnguyen.ums.repo.UserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vietnguyen.ums.exception.ApiException;

import java.time.Instant;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final StatusCodeRepository statusCodeRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, StatusCodeRepository statusCodeRepository,
                       UserRoleRepository userRoleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.statusCodeRepository = statusCodeRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PagedResponse<UserResponse> list(Pageable pageable) {
        Page<UserEntity> page = userRepository.findAllByDeletedAtIsNull(pageable);
        return new PagedResponse<>(page.map(u -> {
            String status = statusCodeRepository.findById(u.getStatusId())
                    .map(StatusCodeEntity::getCode).orElse(null);
            String role = userRoleRepository.findById(u.getRoleId())
                    .map(UserRoleEntity::getCode).orElse(null);
            return UserMapper.toResponse(u, status, role);
        }).getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    public UserResponse get(Long id) {
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        String status = statusCodeRepository.findById(user.getStatusId())
                .map(StatusCodeEntity::getCode).orElse(null);
        String role = userRoleRepository.findById(user.getRoleId())
                .map(UserRoleEntity::getCode).orElse(null);
        return UserMapper.toResponse(user, status, role);
    }

    @Transactional
    public UserResponse create(UserCreateRequest req, Long currentUserId) {
        if (userRepository.existsByUsernameIgnoreCase(req.username())) {
            throw new ApiException(HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS", "Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(req.username());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        Short statusId = resolveStatusId("active", null);
        user.setStatusId(statusId);
        Short roleId = resolveRoleId(req.roleCode(), null);
        user.setRoleId(roleId);
        user.setFailedLoginAttempts(0);
        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setCreatedBy(currentUserId);
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);
        String status = statusCodeRepository.findById(statusId).map(StatusCodeEntity::getCode).orElse(null);
        String role = userRoleRepository.findById(roleId).map(UserRoleEntity::getCode).orElse(null);
        return UserMapper.toResponse(user, status, role);
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        user.setDeletedAt(Instant.now());
        Short inactiveId = resolveStatusId("inactive", "inactive");
        user.setStatusId(inactiveId);
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse changePassword(PasswordChangeRequest req, Long currentUserId) {
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        if (!passwordEncoder.matches(req.oldPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "OLD_PASSWORD_INCORRECT", "Old password incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);
        String status = statusCodeRepository.findById(user.getStatusId()).map(StatusCodeEntity::getCode).orElse(null);
        String role = userRoleRepository.findById(user.getRoleId()).map(UserRoleEntity::getCode).orElse(null);
        return UserMapper.toResponse(user, status, role);
    }

    @Transactional
    public UserResponse resetPassword(Long id, PasswordResetRequest req, Long currentUserId) {
        UserEntity current = userRepository.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CURRENT_USER_NOT_FOUND", "Current user not found"));
        Short adminRoleId = resolveRoleId("admin", null);
        if (!adminRoleId.equals(current.getRoleId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ONLY_ADMIN_CAN_RESET_PASSWORDS", "Only admin can reset passwords");
        }
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);
        String status = statusCodeRepository.findById(user.getStatusId()).map(StatusCodeEntity::getCode).orElse(null);
        String role = userRoleRepository.findById(user.getRoleId()).map(UserRoleEntity::getCode).orElse(null);
        return UserMapper.toResponse(user, status, role);
    }

    private Short resolveStatusId(String code, String defaultCode) {
        String c = code != null ? code : defaultCode;
        if (c == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "STATUS_CODE_REQUIRED", "Status code required");
        }
        return statusCodeRepository.findByDomainAndCode("ums", c)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "INVALID_STATUS_CODE", "Invalid status code"))
                .getId();
    }

    private Short resolveRoleId(String code, String defaultCode) {
        String c = code != null ? code : defaultCode;
        if (c == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ROLE_CODE_REQUIRED", "Role code required");
        }
        return userRoleRepository.findByCode(c)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "INVALID_ROLE_CODE", "Invalid role code"))
                .getId();
    }

    @Transactional
    public UserResponse resetFailedAttempts(Long id, Long currentUserId) {
        UserEntity current = userRepository.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CURRENT_USER_NOT_FOUND", "Current user not found"));
        Short adminRoleId = resolveRoleId("admin", null);
        if (!adminRoleId.equals(current.getRoleId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ONLY_ADMIN_CAN_UNLOCK_ACCOUNTS", "Only admin can unlock accounts");
        }
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        Short lockedId = resolveStatusId("locked", null);
        if (!lockedId.equals(user.getStatusId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "USER_NOT_LOCKED", "User is not locked");
        }
        user.setFailedLoginAttempts(0);
        Short activeId = resolveStatusId("active", "active");
        user.setStatusId(activeId);
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);
        String status = statusCodeRepository.findById(user.getStatusId()).map(StatusCodeEntity::getCode).orElse(null);
        String role = userRoleRepository.findById(user.getRoleId()).map(UserRoleEntity::getCode).orElse(null);
        return UserMapper.toResponse(user, status, role);
    }
}
