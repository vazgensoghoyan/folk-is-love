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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private static final String USERNAME = "user1";
    private static final String EMAIL = "email@example.com";
    private static final String PASSWORD = "pass";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String JWT_TOKEN = "jwt-token";
    private static final String INVALID_USERNAME = "unknown";
    private static final String INVALID_USERNAME_MSG = "Invalid username";
    private static final String INVALID_PASSWORD_MSG = "Invalid password";
    private static final String REGISTER_PASSWORD = "pass123!";
    private static final String INVALID_REGISTER_USERNAME = "bad_user";
    private static final String WRONG_PASSWORD = "wrong";

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
            User user = createActiveUser();

            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
            when(jwtService.generateToken(USERNAME, Role.USER.name())).thenReturn(JWT_TOKEN);

            String token = authService.login(USERNAME, PASSWORD);

            assertEquals(JWT_TOKEN, token);
        }

        @Test
        void loginWithWrongUsernameShouldThrow() {
            when(userRepository.findByUsername(INVALID_USERNAME))
                .thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.login(INVALID_USERNAME, PASSWORD));

            assertEquals(INVALID_USERNAME_MSG, ex.getMessage());
        }

        @Test
        void loginWithWrongPasswordShouldThrow() {
            User user = createActiveUser();

            when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

            when(passwordEncoder.matches(WRONG_PASSWORD, ENCODED_PASSWORD))
                .thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.login(USERNAME, WRONG_PASSWORD));
            assertEquals(INVALID_PASSWORD_MSG, ex.getMessage());
        }

    }

    @Nested
    class RegisterTests {

        @Test
        void registerShouldValidateAndSaveUser() {
            when(passwordEncoder.encode(REGISTER_PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

            User savedUser = createActiveUser();

            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            User result = authService.register(
                USERNAME, EMAIL, REGISTER_PASSWORD
            );

            verify(credentialsValidator).validateUsername(USERNAME);
            verify(credentialsValidator).validatePassword(REGISTER_PASSWORD);
            verify(userRepository).save(any(User.class));

            assertEquals(USERNAME, result.getUsername());
            assertEquals(ENCODED_PASSWORD, result.getPasswordHash());
            assertEquals(Role.USER, result.getRole());
        }

        @Test
        void registerShouldThrowWhenUsernameInvalid() {
            doThrow(new IllegalArgumentException(INVALID_USERNAME_MSG))
                    .when(credentialsValidator).validateUsername(INVALID_REGISTER_USERNAME);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> authService.register(
                            INVALID_REGISTER_USERNAME,
                            EMAIL,
                            REGISTER_PASSWORD
                        ));

            assertEquals(INVALID_USERNAME_MSG, ex.getMessage());
        }

    }

    private User createActiveUser() {
        return User.builder()
            .username(USERNAME)
            .email(EMAIL)
            .passwordHash(ENCODED_PASSWORD)
            .build();
    }
}
