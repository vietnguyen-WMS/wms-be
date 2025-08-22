package com.vietnguyen.ums.security;

import com.vietnguyen.ums.config.AuthCookieProperties;
import com.vietnguyen.ums.config.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieUtil {
    private final AuthCookieProperties cookieProps;
    private final JwtProperties jwtProps;

    public CookieUtil(AuthCookieProperties cookieProps, JwtProperties jwtProps) {
        this.cookieProps = cookieProps;
        this.jwtProps = jwtProps;
    }

    public ResponseCookie buildAuthCookie(String token) {
        return ResponseCookie.from(cookieProps.getName(), token)
                .domain(cookieProps.getDomain())
                .path(cookieProps.getPath())
                .httpOnly(cookieProps.isHttpOnly())
                .secure(cookieProps.isSecure())
                .sameSite(cookieProps.getSameSite())
                .maxAge(Duration.ofMinutes(jwtProps.getExpirationMinutes()))
                .build();
    }

    public ResponseCookie buildDeletionCookie() {
        return ResponseCookie.from(cookieProps.getName(), "")
                .domain(cookieProps.getDomain())
                .path(cookieProps.getPath())
                .httpOnly(cookieProps.isHttpOnly())
                .secure(cookieProps.isSecure())
                .sameSite(cookieProps.getSameSite())
                .maxAge(0)
                .build();
    }

    public Optional<String> readAuthCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(cookieProps.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
