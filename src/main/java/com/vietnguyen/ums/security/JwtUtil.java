package com.vietnguyen.ums.security;

import com.vietnguyen.ums.config.JwtProperties;
import com.vietnguyen.ums.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final JwtProperties props;

    public JwtUtil(JwtProperties props) {
        this.props = props;
    }

    public String generateToken(UserEntity user) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getExpirationMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(user.getId() + ":" + user.getUsername())
                .claim("uid", user.getId())
                .claim("uname", user.getUsername())
                .claim("status", user.getStatusId())
                .expiration(Date.from(exp))
                .issuedAt(Date.from(now))
                .signWith(Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token);
    }

    public boolean isExpired(Jws<Claims> jws) {
        Date exp = jws.getPayload().getExpiration();
        return exp.before(new Date());
    }
}
