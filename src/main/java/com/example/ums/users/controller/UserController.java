package com.example.ums.users.controller;

import com.example.ums.users.dto.CreateUserRequest;
import com.example.ums.users.dto.UpdateUserRequest;
import com.example.ums.users.dto.UserResponse;
import com.example.ums.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Search users")
    public Page<UserResponse> search(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size,
                                     @RequestParam(required = false) String q,
                                     @RequestParam(defaultValue = "false") boolean includeDeleted) {
        return userService.search(page, size, q, includeDeleted);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PostMapping
    @Operation(summary = "Create user")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PatchMapping("/{id}")
    @Operation(summary = "Update user")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user (soft)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
