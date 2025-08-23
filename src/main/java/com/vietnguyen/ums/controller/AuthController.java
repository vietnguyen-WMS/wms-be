package com.vietnguyen.ums.controller;

import com.vietnguyen.ums.dto.LoginRequest;
import com.vietnguyen.ums.dto.LoginResponse;
import com.vietnguyen.ums.dto.UserInfo;
import com.vietnguyen.ums.security.AuthUser;
import com.vietnguyen.ums.security.CookieUtil;
import com.vietnguyen.ums.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.vietnguyen.ums.exception.ApiException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    public AuthController(AuthService authService, CookieUtil cookieUtil) {
        this.authService = authService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse result = authService.login(request.username(), request.password());
        ResponseCookie cookie = cookieUtil.buildAuthCookie(result.token());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public UserInfo me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUser user)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthorized");
        }
        return authService.getUserInfo(user.id());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = cookieUtil.buildDeletionCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }
}
