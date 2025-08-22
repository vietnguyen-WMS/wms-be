package com.vietnguyen.ums.controller;

import com.vietnguyen.ums.dto.*;
import com.vietnguyen.ums.security.AuthUser;
import com.vietnguyen.ums.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public PagedResponse<UserResponse> list(Pageable pageable) {
        return userService.list(pageable);
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserCreateRequest req, Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        return userService.create(req, user.id());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        userService.delete(id, user.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/password")
    public UserResponse resetPassword(@PathVariable Long id, @Valid @RequestBody PasswordResetRequest req,
                                      Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        return userService.resetPassword(id, req, user.id());
    }

    @PostMapping("/{id}/reset-failed-attempts")
    public UserResponse resetFailedAttempts(@PathVariable Long id, Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        return userService.resetFailedAttempts(id, user.id());
    }
}
