package com.example.ums.controller;

import com.example.ums.dto.*;
import com.example.ums.security.AuthUser;
import com.example.ums.service.UserService;
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

    @PutMapping("/{id}")
    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UserUpdateRequest req, Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        return userService.update(id, req, user.id());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        userService.delete(id, user.id());
        return ResponseEntity.noContent().build();
    }
}
