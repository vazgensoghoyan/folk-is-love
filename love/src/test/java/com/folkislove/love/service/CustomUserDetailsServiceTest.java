package com.folkislove.love.service;

import com.folkislove.love.model.User;
import com.folkislove.love.model.User.Role;
import com.folkislove.love.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;

    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);

        service = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetails() {
        var user = User.builder()
            .username("user1")
            .passwordHash("encodedPass")
            .role(Role.USER)
            .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        var userDetails = service.loadUserByUsername("user1");

        assertEquals("user1", userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsernameShouldThrowIfUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        var exc = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknown"));

        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void loadUserByUsernameShouldReturnAdminRole() {
        var admin = User.builder()
            .username("admin")
            .passwordHash("adminPass")
            .role(Role.ADMIN)
            .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        var userDetails = service.loadUserByUsername("admin");

        assertEquals("admin", userDetails.getUsername());
        assertEquals("adminPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }
}
