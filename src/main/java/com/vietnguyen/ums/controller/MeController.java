package com.vietnguyen.ums.controller;

import com.vietnguyen.ums.dto.PasswordChangeRequest;
import com.vietnguyen.ums.dto.UserResponse;
import com.vietnguyen.ums.security.AuthUser;
import com.vietnguyen.ums.service.UserService;
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
