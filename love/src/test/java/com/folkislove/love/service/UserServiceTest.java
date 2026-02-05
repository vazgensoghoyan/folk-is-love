package com.folkislove.love.service;

import com.folkislove.love.dto.response.UserResponse;
import com.folkislove.love.mapper.UserMapper;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;
import com.folkislove.love.repository.TagRepository;
import com.folkislove.love.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private TagRepository tagRepository;
    private CurrentUserService currentUserService;
    private UserMapper userMapper;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tagRepository = mock(TagRepository.class);
        currentUserService = mock(CurrentUserService.class);
        userMapper = mock(UserMapper.class);

        userService = new UserService(userRepository, tagRepository, currentUserService, userMapper);
    }

    @Nested
    class GetCurrentUserTests {

        @Test
        void shouldReturnMappedUser() {
            var user = User.builder().username("user1").build();
            var dto = UserResponse.builder().username("user1").build();

            when(currentUserService.getCurrentUser()).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(dto);

            var result = userService.getCurrentUser();

            assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("user1", result.getUsername())
            );
        }
    }

    @Nested
    class GetUserByUsernameTests {

        @Test
        void shouldReturnUserIfFound() {
            var user = User.builder().username("user1").build();
            var dto = UserResponse.builder().username("user1").build();

            when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
            when(userMapper.toDto(user)).thenReturn(dto);

            var result = userService.getUserByUsername("user1");

            assertEquals("user1", result.getUsername());
        }

        @Test
        void shouldThrowIfUserNotFound() {
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userService.getUserByUsername("unknown"));

            assertEquals("User not found", ex.getMessage());
        }
    }

    @Nested
    class AddInterestTests {

        @Test
        void shouldAddInterestAndSaveUser() {
            var user = User.builder().build();
            var tag = Tag.builder().id(1L).build();

            when(currentUserService.getCurrentUser()).thenReturn(user);
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
            when(userRepository.save(user)).thenReturn(user);

            userService.addInterest(1L);

            assertTrue(user.getInterests().contains(tag));
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void shouldNotDuplicateInterest() {
            var tag = Tag.builder().id(1L).build();
            var user = User.builder()
                .interests(new HashSet<>(Set.of(tag)))
                .build();

            when(currentUserService.getCurrentUser()).thenReturn(user);
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
            when(userRepository.save(user)).thenReturn(user);

            userService.addInterest(1L);

            assertEquals(1, user.getInterests().size());
            assertTrue(user.getInterests().contains(tag));
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void shouldThrowIfTagNotFound() {
            var user = User.builder().interests(Set.of()).build();
            
            when(currentUserService.getCurrentUser()).thenReturn(user);
            when(tagRepository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userService.addInterest(1L));

            assertEquals("Tag not found", ex.getMessage());
        }
    }

    @Nested
    class RemoveInterestTests {

        @Test
        void shouldRemoveInterestAndSaveUser() {
            var tag = Tag.builder().id(1L).build();
            var user = User.builder()
                    .interests(new HashSet<>(Set.of(tag)))
                    .build();

            when(currentUserService.getCurrentUser()).thenReturn(user);
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
            when(userRepository.save(user)).thenReturn(user);

            userService.removeInterest(1L);

            assertFalse(user.getInterests().contains(tag));
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void shouldThrowIfTagNotFound() {
            var user = User.builder().interests(Set.of()).build();

            when(currentUserService.getCurrentUser()).thenReturn(user);
            when(tagRepository.findById(1L)).thenReturn(Optional.empty());

            var exc = assertThrows(RuntimeException.class,
                    () -> userService.removeInterest(1L));

            assertEquals("Tag not found", exc.getMessage());
        }
    }

    @Nested
    class AddInterestEdgeCasesTests {

        @Test
        void shouldDoNothingIfUserAlreadyHasTag() {
            var tag = Tag.builder().id(1L).build();
            var user = User.builder().interests(new HashSet<>(Set.of(tag))).build();

            when(currentUserService.getCurrentUser()).thenReturn(user);
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

            userService.addInterest(1L);

            // Проверяем, что тег остался один и сохранение вызвано
            assertEquals(1, user.getInterests().size());
            assertTrue(user.getInterests().contains(tag));
            verify(userRepository, times(1)).save(user);
        }
    }

    @Nested
    class RemoveInterestEdgeCasesTests {

        @Test
        void shouldDoNothingIfUserDoesNotHaveTag() {
            var tag = Tag.builder().id(1L).build();
            var user = User.builder().interests(new HashSet<>()).build();

            when(currentUserService.getCurrentUser()).thenReturn(user);
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

            userService.removeInterest(1L);

            // Проверяем, что список интересов по-прежнему пуст
            assertTrue(user.getInterests().isEmpty());
            verify(userRepository, times(1)).save(user);
        }
    }
}
