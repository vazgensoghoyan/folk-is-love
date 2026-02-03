package com.folkislove.love.service;

import com.folkislove.love.model.User;
import com.folkislove.love.model.User.Role;
import com.folkislove.love.repository.UserRepository;
import com.folkislove.love.util.UserCredentialsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private UserCredentialsValidator credentialsValidator;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        credentialsValidator = mock(UserCredentialsValidator.class);

        authService = new AuthService(userRepository, passwordEncoder, jwtService, credentialsValidator);
    }

    @Nested
    class LoginTests {

        @Test
        void loginWithValidCredentialsShouldReturnToken() {
            User user = User.builder()
                    .username("user1")
                    .passwordHash("encodedPass")
                    .banned(false)
                    .role(Role.USER)
                    .build();

            when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);
            when(jwtService.generateToken("user1", "USER")).thenReturn("jwt-token");

            String token = authService.login("user1", "pass");

            assertEquals("jwt-token", token);
        }

        @Test
        void loginWithWrongUsernameShouldThrow() {
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.login("unknown", "pass"));
            assertEquals("Invalid username", ex.getMessage());
        }

        @Test
        void loginWithBannedUserShouldThrow() {
            User user = User.builder().username("user1").banned(true).build();
            when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.login("user1", "pass"));
            assertEquals("User is banned", ex.getMessage());
        }

        @Test
        void loginWithWrongPasswordShouldThrow() {
            User user = User.builder().username("user1").passwordHash("encodedPass").banned(false).build();
            when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.login("user1", "wrong"));
            assertEquals("Invalid password", ex.getMessage());
        }

    }

    @Nested
    class RegisterTests {

        @Test
        void registerShouldValidateAndSaveUser() {
            when(passwordEncoder.encode("pass123!")).thenReturn("encodedPass");

            User savedUser = User.builder().username("user1").passwordHash("encodedPass").role(Role.USER).build();
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            User result = authService.register("user1", "pass123!");

            verify(credentialsValidator).validateUsername("user1");
            verify(credentialsValidator).validatePassword("pass123!");
            verify(userRepository).save(any(User.class));

            assertEquals("user1", result.getUsername());
            assertEquals("encodedPass", result.getPasswordHash());
            assertEquals(Role.USER, result.getRole());
        }

        @Test
        void registerShouldThrowWhenUsernameInvalid() {
            doThrow(new IllegalArgumentException("Invalid username"))
                    .when(credentialsValidator).validateUsername("bad_user");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> authService.register("bad_user", "pass123!"));
            assertEquals("Invalid username", ex.getMessage());
        }

    }
}
