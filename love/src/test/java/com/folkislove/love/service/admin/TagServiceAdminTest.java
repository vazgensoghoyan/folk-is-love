package com.folkislove.love.service.admin;

import com.folkislove.love.model.Post;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.TagRepository;
import com.folkislove.love.service.CurrentUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceAdminTest {

    private TagRepository tagRepository;
    private CurrentUserService currentUserService;
    private TagServiceAdmin tagServiceAdmin;

    @BeforeEach
    void setUp() {
        tagRepository = mock(TagRepository.class);
        currentUserService = mock(CurrentUserService.class);
        tagServiceAdmin = new TagServiceAdmin(tagRepository, currentUserService);
    }

    @Nested
    class CreateTagTests {

        @Test
        void shouldCreateTagSuccessfully() {
            when(currentUserService.isAdmin()).thenReturn(true);
            when(tagRepository.existsByNameIgnoreCase("Folk")).thenReturn(false);

            Tag savedTag = Tag.builder().id(1L).name("Folk").build();
            when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

            Tag result = tagServiceAdmin.createTag(" Folk ");

            assertEquals("Folk", result.getName());
            assertEquals(1L, result.getId());
            verify(tagRepository).save(any(Tag.class));
        }

        @Test
        void shouldThrowIfNotAdmin() {
            when(currentUserService.isAdmin()).thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> tagServiceAdmin.createTag("NewTag"));

            assertEquals("You don't have permission to access this resource", ex.getMessage());
            verify(tagRepository, never()).save(any());
        }

        @Test
        void shouldThrowIfTagAlreadyExists() {
            when(currentUserService.isAdmin()).thenReturn(true);
            when(tagRepository.existsByNameIgnoreCase("Folk")).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> tagServiceAdmin.createTag("Folk"));

            assertEquals("Tag already exists", ex.getMessage());
            verify(tagRepository, never()).save(any());
        }
    }

    @Nested
    class RenameTagTests {

        @Test
        void shouldRenameTagSuccessfully() {
            when(currentUserService.isAdmin()).thenReturn(true);

            Tag existing = Tag.builder().id(1L).name("Old").build();
            when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(tagRepository.existsByNameIgnoreCase("New")).thenReturn(false);

            Tag result = tagServiceAdmin.renameTag(1L, " New ");

            assertEquals("New", result.getName());
            assertEquals(existing, result); // проверяем, что изменился сам объект
        }

        @Test
        void shouldThrowIfNotAdmin() {
            when(currentUserService.isAdmin()).thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> tagServiceAdmin.renameTag(1L, "New"));

            assertEquals("You don't have permission to access this resource", ex.getMessage());
        }

        @Test
        void shouldThrowIfTagNotFound() {
            when(currentUserService.isAdmin()).thenReturn(true);
            when(tagRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> tagServiceAdmin.renameTag(1L, "New"));

            assertEquals("Tag not found: 1", ex.getMessage());
        }

        @Test
        void shouldThrowIfNewNameAlreadyExists() {
            when(currentUserService.isAdmin()).thenReturn(true);

            Tag existing = Tag.builder().id(1L).name("Old").build();
            when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(tagRepository.existsByNameIgnoreCase("New")).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> tagServiceAdmin.renameTag(1L, "New"));

            assertEquals("Another tag with this name already exists", ex.getMessage());
        }
    }

    @Nested
    class DeleteTagTests {

        @Test
        void shouldDeleteTagSuccessfully() {
            when(currentUserService.isAdmin()).thenReturn(true);

            Tag tag = Tag.builder().id(1L).name("Folk").build();
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

            tagServiceAdmin.deleteTag(1L);

            verify(tagRepository).delete(tag);
        }

        @Test
        void shouldThrowIfNotAdmin() {
            when(currentUserService.isAdmin()).thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> tagServiceAdmin.deleteTag(1L));

            assertEquals("You don't have permission to access this resource", ex.getMessage());
            verify(tagRepository, never()).delete(any());
        }

        @Test
        void shouldThrowIfTagNotFound() {
            when(currentUserService.isAdmin()).thenReturn(true);
            when(tagRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> tagServiceAdmin.deleteTag(1L));

            assertEquals("Tag not found: 1", ex.getMessage());
            verify(tagRepository, never()).delete(any());
        }

        @Test
        void shouldThrowIfTagInUse() {
            when(currentUserService.isAdmin()).thenReturn(true);

            Tag tag = Tag.builder()
                    .id(1L)
                    .posts(new HashSet<>(Set.of(mock(Post.class))))
                    .build();

            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> tagServiceAdmin.deleteTag(1L));

            assertEquals("Cannot delete tag that is in use. Merge it instead.", ex.getMessage());
            verify(tagRepository, never()).delete(any());
        }
    }
}
