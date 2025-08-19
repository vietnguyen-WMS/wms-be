package com.example.ums.service;

import com.example.ums.dto.*;
import com.example.ums.entity.StatusCodeEntity;
import com.example.ums.entity.UserEntity;
import com.example.ums.entity.UserRoleEntity;
import com.example.ums.repo.StatusCodeRepository;
import com.example.ums.repo.UserRepository;
import com.example.ums.repo.UserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String status = statusCodeRepository.findById(user.getStatusId())
                .map(StatusCodeEntity::getCode).orElse(null);
        String role = userRoleRepository.findById(user.getRoleId())
                .map(UserRoleEntity::getCode).orElse(null);
        return UserMapper.toResponse(user, status, role);
    }

    @Transactional
    public UserResponse create(UserCreateRequest req, Long currentUserId) {
        if (userRepository.existsByUsernameIgnoreCase(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(req.username());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        Short statusId = resolveStatusId(req.statusCode(), "active");
        user.setStatusId(statusId);
        Short roleId = resolveRoleId(req.roleCode(), "operator");
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
    public UserResponse update(Long id, UserUpdateRequest req, Long currentUserId) {
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (req.username() != null && !req.username().equalsIgnoreCase(user.getUsername())) {
            if (userRepository.existsByUsernameIgnoreCase(req.username())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            }
            user.setUsername(req.username());
        }
        if (req.password() != null) {
            user.setPasswordHash(passwordEncoder.encode(req.password()));
        }
        if (req.statusCode() != null) {
            user.setStatusId(resolveStatusId(req.statusCode(), null));
        }
        if (req.roleCode() != null) {
            user.setRoleId(resolveRoleId(req.roleCode(), null));
        }
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);
        String status = statusCodeRepository.findById(user.getStatusId()).map(StatusCodeEntity::getCode).orElse(null);
        String role = userRoleRepository.findById(user.getRoleId()).map(UserRoleEntity::getCode).orElse(null);
        return UserMapper.toResponse(user, status, role);
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setDeletedAt(Instant.now());
        Short inactiveId = resolveStatusId("inactive", "inactive");
        user.setStatusId(inactiveId);
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);
    }

    private Short resolveStatusId(String code, String defaultCode) {
        String c = code != null ? code : defaultCode;
        if (c == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status code required");
        }
        return statusCodeRepository.findByDomainAndCode("ums", c)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status code"))
                .getId();
    }

    private Short resolveRoleId(String code, String defaultCode) {
        String c = code != null ? code : defaultCode;
        if (c == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role code required");
        }
        return userRoleRepository.findByCode(c)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role code"))
                .getId();
    }

    @Transactional
    public UserResponse resetFailedAttempts(Long id, Long currentUserId) {
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Short adminRoleId = resolveRoleId("admin", null);
        if (!adminRoleId.equals(user.getRoleId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not admin");
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
