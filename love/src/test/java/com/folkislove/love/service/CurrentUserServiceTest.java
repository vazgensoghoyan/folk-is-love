package com.folkislove.love.service;

import com.folkislove.love.model.User;
import com.folkislove.love.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrentUserServiceTest {

    private UserRepository userRepository;
    private CurrentUserService currentUserService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        currentUserService = new CurrentUserService(userRepository);
    }

    @Nested
    class GetCurrentUsernameTests {

        @Test
        void getCurrentUsernameFromUserDetails() {
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("user1");

            Authentication auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(userDetails);
            when(auth.isAuthenticated()).thenReturn(true);

            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);

            try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
                holder.when(SecurityContextHolder::getContext).thenReturn(context);

                String username = currentUserService.getCurrentUsername();
                assertEquals("user1", username);
            }
        }

        @Test
        void getCurrentUsernameFromAuthName() {
            Authentication auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn("principal");
            when(auth.getName()).thenReturn("principalName");
            when(auth.isAuthenticated()).thenReturn(true);

            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);

            try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
                holder.when(SecurityContextHolder::getContext).thenReturn(context);

                String username = currentUserService.getCurrentUsername();
                assertEquals("principalName", username);
            }
        }

        @Test
        void getCurrentUsernameThrowsIfNotAuthenticated() {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(false);

            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);

            try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
                holder.when(SecurityContextHolder::getContext).thenReturn(context);

                RuntimeException ex = assertThrows(RuntimeException.class,
                        () -> currentUserService.getCurrentUsername());
                assertEquals("User is not authenticated", ex.getMessage());
            }
        }

    }

    @Nested
    class GetCurrentUserTests {

        @Test
        void getCurrentUserReturnsUser() {
            User user = User.builder().username("user1").build();

            try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
                Authentication auth = mock(Authentication.class);
                when(auth.getPrincipal()).thenReturn("user1");
                when(auth.getName()).thenReturn("user1");
                when(auth.isAuthenticated()).thenReturn(true);

                SecurityContext context = mock(SecurityContext.class);
                when(context.getAuthentication()).thenReturn(auth);
                holder.when(SecurityContextHolder::getContext).thenReturn(context);

                when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

                User result = currentUserService.getCurrentUser();
                assertEquals("user1", result.getUsername());
            }
        }

        @Test
        void getCurrentUserThrowsIfNotFound() {
            try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
                Authentication auth = mock(Authentication.class);
                when(auth.getPrincipal()).thenReturn("user1");
                when(auth.getName()).thenReturn("user1");
                when(auth.isAuthenticated()).thenReturn(true);

                SecurityContext context = mock(SecurityContext.class);
                when(context.getAuthentication()).thenReturn(auth);
                holder.when(SecurityContextHolder::getContext).thenReturn(context);

                when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

                RuntimeException ex = assertThrows(RuntimeException.class,
                        () -> currentUserService.getCurrentUser());
                assertEquals("User not found", ex.getMessage());
            }
        }

    }

    @Nested
    class IsAdminTests {

        @Test
        void isAdminReturnsTrueForAdmin() {
            User admin = User.builder().username("admin").role(User.Role.ADMIN).build();

            CurrentUserService spyService = spy(currentUserService);
            doReturn("admin").when(spyService).getCurrentUsername();
            doReturn(admin).when(spyService).getCurrentUser();

            assertTrue(spyService.isAdmin());
        }

        @Test
        void isAdminReturnsFalseForUser() {
            User user = User.builder().username("user1").role(User.Role.USER).build();

            CurrentUserService spyService = spy(currentUserService);
            doReturn(user.getUsername()).when(spyService).getCurrentUsername();
            doReturn(user).when(spyService).getCurrentUser();

            assertFalse(spyService.isAdmin());
        }

    }
    
    @Nested 
    class IsOwnerOrAdminTests {

        @Test
        void isOwnerOrAdminReturnsTrueIfOwner() {
            CurrentUserService spyService = spy(currentUserService);
            doReturn("user1").when(spyService).getCurrentUsername();
            doReturn(User.builder().role(User.Role.USER).build()).when(spyService).getCurrentUser();

            assertTrue(spyService.isOwnerOrAdmin("user1"));
        }

        @Test
        void isOwnerOrAdminReturnsTrueIfAdmin() {
            User admin = User.builder().role(User.Role.ADMIN).build();
            CurrentUserService spyService = spy(currentUserService);
            doReturn("someoneElse").when(spyService).getCurrentUsername();
            doReturn(admin).when(spyService).getCurrentUser();

            assertTrue(spyService.isOwnerOrAdmin("user1"));
        }

    }

    @Nested
    class CheckOwnerOrAdminTests {

        @Test
        void checkOwnerOrAdminThrowsIfNotOwnerOrAdmin() {
            User user = User.builder().role(User.Role.USER).build();
            CurrentUserService spyService = spy(currentUserService);
            doReturn("someoneElse").when(spyService).getCurrentUsername();
            doReturn(user).when(spyService).getCurrentUser();

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> spyService.checkOwnerOrAdmin("user1"));
            assertEquals("You don't have permission to access this resource", ex.getMessage());
        }

    }
    
}
