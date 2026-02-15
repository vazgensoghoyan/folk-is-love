package com.folkislove.love.service;

import com.folkislove.love.exception.JwtAuthenticationException;
import com.folkislove.love.model.User;
import com.folkislove.love.model.User.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "MySuperSecretKeyForJwtTesting123456!";
    private static final long EXPIRATION_MS = 1000; // 1 секунда

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);
    }

    @Test
    void generateTokenShouldIncludeUsernameAndRole() {
        User user = generateUser();

        String token = jwtService.generateToken(user);
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        Role role = jwtService.extractRole(token);

        assertEquals(user.getUsername(), username);
        assertEquals(user.getRole(), role);
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {
        User user = generateUser();
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user.getUsername()));
    }

    @Test
    void isTokenValidShouldReturnFalseForWrongUsername() {
        User user = generateUser();
        String token = jwtService.generateToken(user);
        assertFalse(jwtService.isTokenValid(token, "otherUser"));
    }

    @Test
    void isTokenValidShouldReturnFalseForExpiredToken() throws InterruptedException {
        var shortLivedService = new JwtService(SECRET, 100); // 0.1 секунда
        User user = generateUser();
        String token = shortLivedService.generateToken(user);

        // Ждем пока токен истечет
        Thread.sleep(200);

        assertThrows(JwtAuthenticationException.class, () -> 
            shortLivedService.isTokenValid(token, user.getUsername())
        );
    }

    @Test
    void extractUsernameShouldThrowForInvalidToken() {
        String invalidToken = "invalid.token.value";
        var exc = assertThrows(RuntimeException.class,
                () -> jwtService.extractUsername(invalidToken));
        assertTrue(exc.getMessage().contains("Invalid JWT token"));
    }

    @Test
    void extractRoleShouldThrowForInvalidToken() {
        String invalidToken = "invalid.token.value";
        var exc = assertThrows(RuntimeException.class,
                () -> jwtService.extractRole(invalidToken));
        assertTrue(exc.getMessage().contains("Invalid JWT token"));
    }

    // private helper

    User generateUser() {
        return User
            .builder()
            .username("user1")
            .role(Role.USER)
            .build();
    }
}
