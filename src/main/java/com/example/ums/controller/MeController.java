package com.example.ums.controller;

import com.example.ums.dto.PasswordChangeRequest;
import com.example.ums.dto.UserResponse;
import com.example.ums.security.AuthUser;
import com.example.ums.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class MeController {
    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/password")
    public UserResponse changePassword(@Valid @RequestBody PasswordChangeRequest req, Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        return userService.changePassword(req, user.id());
    }
}
