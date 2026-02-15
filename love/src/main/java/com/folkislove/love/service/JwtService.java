package com.folkislove.love.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.folkislove.love.exception.JwtAuthenticationException;
import com.folkislove.love.model.User;
import com.folkislove.love.model.User.Role;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long expirationMs;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration-ms}") long expiration
    ) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        expirationMs = expiration;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(user.getUsername())
            .claim("role", user.getRole().name())
            .issuer("folkislove-api")
            .audience().add("folkislove-client").and()
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Role extractRole(String token) {
        String role = parseClaims(token).get("role", String.class);
        return Role.valueOf(role);
    }

    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            if (!"folkislove-api".equals(claims.getIssuer())) {
                throw new JwtAuthenticationException("Invalid JWT issuer");
            }
            
            Set<String> audience = claims.getAudience();
            if (audience == null || !audience.contains("folkislove-client")) {
                throw new JwtAuthenticationException("Invalid JWT audience");
            }

            return claims;

        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("JWT token expired");
            
        } catch (JwtException e) {
            throw new JwtAuthenticationException("Invalid JWT token");
        }
    }
}
