package com.folkislove.love.service;

import com.folkislove.love.exception.custom.ResourceNotFoundException;
import com.folkislove.love.exception.custom.TagAlreadyExistsException;
import com.folkislove.love.exception.custom.TagInUseException;
import com.folkislove.love.model.Post;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CurrentUserService currentUserService;

    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tagService = new TagService(tagRepository, currentUserService);
    }

    @Nested
    class GetTagByIdTests {

        @Test
        void returnsTagIfExists() {
            Tag tag = Tag.builder().id(1L).name("Music").build();
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

            Tag result = tagService.getTagById(1L);

            assertEquals(tag, result);
        }

        @Test
        void throwsIfNotFound() {
            when(tagRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> tagService.getTagById(1L));
        }
    }

    @Nested
    class GetAllTagsTests {

        @Test
        void returnsPageOfTags() {
            Tag tag1 = Tag.builder().id(1L).name("Music").build();
            Tag tag2 = Tag.builder().id(2L).name("Dance").build();
            Page<Tag> page = new PageImpl<>(List.of(tag1, tag2));

            when(tagRepository.findAll(any(Pageable.class))).thenReturn(page);

            Page<Tag> result = tagService.getAllTags(Pageable.unpaged());

            assertEquals(2, result.getContent().size());
        }
    }

    @Nested
    class CreateTagTests {

        @Test
        void createsTagSuccessfully() {
            doNothing().when(currentUserService).checkIsAdmin();
            when(tagRepository.existsByNameIgnoreCase("Music")).thenReturn(false);
            Tag savedTag = Tag.builder().id(1L).name("Music").build();
            when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

            Tag result = tagService.createTag("  Music  "); // тест нормализации

            assertEquals("Music", result.getName());
            verify(tagRepository).save(any(Tag.class));
        }

        @Test
        void throwsIfTagExists() {
            doNothing().when(currentUserService).checkIsAdmin();
            when(tagRepository.existsByNameIgnoreCase("Music")).thenReturn(true);

            assertThrows(TagAlreadyExistsException.class, () -> tagService.createTag("Music"));
        }
    }

    @Nested
    class RenameTagTests {

        @Test
        void renamesSuccessfully() {
            doNothing().when(currentUserService).checkIsAdmin();
            Tag tag = Tag.builder().id(1L).name("Old").build();
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
            when(tagRepository.existsByNameIgnoreCase("New")).thenReturn(false);

            Tag result = tagService.renameTag(1L, "  New  ");

            assertEquals("New", result.getName());
        }

        @Test
        void throwsIfTagNotFound() {
            doNothing().when(currentUserService).checkIsAdmin();
            when(tagRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> tagService.renameTag(1L, "New"));
        }

        @Test
        void throwsIfNewNameExists() {
            doNothing().when(currentUserService).checkIsAdmin();
            Tag tag = Tag.builder().id(1L).name("Old").build();
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
            when(tagRepository.existsByNameIgnoreCase("New")).thenReturn(true);

            assertThrows(TagAlreadyExistsException.class, () -> tagService.renameTag(1L, "New"));
        }
    }

    @Nested
    class DeleteTagTests {

        @Test
        void deletesSuccessfully() {
            doNothing().when(currentUserService).checkIsAdmin();
            Tag tag = Tag.builder().id(1L).name("Music").build();
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

            tagService.deleteTag(1L);

            verify(tagRepository).delete(tag);
        }

        @Test
        void throwsIfTagNotFound() {
            doNothing().when(currentUserService).checkIsAdmin();
            when(tagRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> tagService.deleteTag(1L));
        }

        @Test
        void throwsIfTagInUse() {
            doNothing().when(currentUserService).checkIsAdmin();

            Tag tag = Tag.builder().id(1L).name("Music").build();
            tag.getPosts().add(mock(Post.class));

            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

            assertThrows(TagInUseException.class, () -> tagService.deleteTag(1L));
        }
    }
}
