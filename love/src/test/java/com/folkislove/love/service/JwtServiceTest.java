package com.folkislove.love.service;

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
        String token = jwtService.generateToken("user1", "USER");
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        Role role = jwtService.extractRole(token);

        assertEquals("user1", username);
        assertEquals(Role.USER, role);
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("user1", "USER");
        assertTrue(jwtService.isTokenValid(token, "user1"));
    }

    @Test
    void isTokenValidShouldReturnFalseForWrongUsername() {
        String token = jwtService.generateToken("user1", "USER");
        assertFalse(jwtService.isTokenValid(token, "otherUser"));
    }

    @Test
    void isTokenValidShouldReturnFalseForExpiredToken() throws InterruptedException {
        var shortLivedService = new JwtService(SECRET, 100); // 0.1 секунда
        String token = shortLivedService.generateToken("user1", "USER");

        // Ждем пока токен истечет
        Thread.sleep(200);

        assertFalse(shortLivedService.isTokenValid(token, "user1"));
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
}
