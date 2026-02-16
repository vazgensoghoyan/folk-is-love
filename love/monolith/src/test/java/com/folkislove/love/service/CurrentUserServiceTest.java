package com.folkislove.love.service;

import com.folkislove.common.enums.Role;
import com.folkislove.love.exception.custom.AccessDeniedException;
import com.folkislove.love.exception.custom.AuthorizationException;
import com.folkislove.love.exception.custom.ResourceNotFoundException;
import com.folkislove.love.model.User;
import com.folkislove.love.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private CurrentUserService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CurrentUserService(userRepository);
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    class GetCurrentUsernameTests {

        @Test
        void returnsUsernameFromUserDetails() {
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("john");

            String username = service.getCurrentUsername();

            assertEquals("john", username);
        }

        @Test
        void returnsNameFromAuthenticationWhenPrincipalIsNotUserDetails() {
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("anon");
            when(authentication.getName()).thenReturn("johnDoe");

            String username = service.getCurrentUsername();

            assertEquals("johnDoe", username);
        }

        @Test
        void throwsAuthorizationExceptionIfAuthIsNull() {
            when(securityContext.getAuthentication()).thenReturn(null);

            assertThrows(AuthorizationException.class, () -> service.getCurrentUsername());
        }

        @Test
        void throwsAuthorizationExceptionForAnonymousUserPrincipal() {
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("anonymousUser");

            assertThrows(AuthorizationException.class, () -> service.getCurrentUsername());
        }
    }

    @Nested
    class GetCurrentUserTests {

        @Test
        void returnsUserFromRepository() {
            User user = User.builder().username("john").role(Role.USER).build();
            mockAuthAs("john");

            when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

            User result = service.getCurrentUser();
            assertEquals(user, result);
        }

        @Test
        void throwsResourceNotFoundExceptionIfUserNotFound() {
            mockAuthAs("john");

            when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.getCurrentUser());
        }
    }

    @Nested
    class RoleChecksTests {

        private User admin;
        private User user;

        @BeforeEach
        void initUsers() {
            admin = User.builder().username("admin").role(Role.ADMIN).build();
            user = User.builder().username("user").role(Role.USER).build();
        }

        @Test
        void isAdmin_returnsTrueForAdmin() {
            mockCurrentUser(admin);

            assertTrue(service.isAdmin());
        }

        @Test
        void isAdmin_returnsFalseForUser() {
            mockCurrentUser(user);

            assertFalse(service.isAdmin());
        }

        @Test
        void isOwner_returnsTrueIfUsernameMatches() {
            mockAuthAs("user");

            assertTrue(service.isOwner("user"));
            assertFalse(service.isOwner("other"));
        }

        @Test
        void isOwnerOrAdmin_returnsTrueIfOwnerOrAdmin() {
            mockCurrentUser(admin);
            mockAuthAs("admin");
            assertTrue(service.isOwnerOrAdmin("anyone")); // admin check
            assertTrue(service.isOwnerOrAdmin("admin"));  // owner check

            mockCurrentUser(user);
            mockAuthAs("user");
            assertTrue(service.isOwnerOrAdmin("user")); // owner
            assertFalse(service.isOwnerOrAdmin("other")); // not owner or admin
        }

        @Test
        void checkIsAdmin_throwsForNonAdmin() {
            mockCurrentUser(user);
            mockAuthAs("user");

            assertThrows(AccessDeniedException.class, () -> service.checkIsAdmin());
        }

        @Test
        void checkIsOwner_throwsForNonOwner() {
            mockAuthAs("user");

            assertThrows(AccessDeniedException.class, () -> service.checkIsOwner("other"));
        }

        @Test
        void checkIsOwnerOrAdmin_throwsWhenNeither() {
            mockCurrentUser(user);
            mockAuthAs("user");

            assertThrows(AccessDeniedException.class, () -> service.checkIsOwnerOrAdmin("other"));
        }

        @Test
        void checkIsOwnerOrAdmin_doesNotThrowForOwnerOrAdmin() {
            mockCurrentUser(admin);
            mockAuthAs("admin");
            assertDoesNotThrow(() -> service.checkIsOwnerOrAdmin("anyone"));

            mockCurrentUser(user);
            mockAuthAs("user");
            assertDoesNotThrow(() -> service.checkIsOwnerOrAdmin("user"));
        }
    }

    // private helpers

    private void mockCurrentUser(User userObj) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(userObj.getUsername());
        when(userRepository.findByUsername(userObj.getUsername())).thenReturn(Optional.of(userObj));
    }

    private void mockAuthAs(String username) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
    }
}
