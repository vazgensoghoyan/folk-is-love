package com.folkislove.love.service.admin;

import com.folkislove.love.model.*;
import com.folkislove.love.repository.TagRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceAdminTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceAdmin tagServiceAdmin;

    private Tag sourceTag;
    private Tag targetTag;

    @BeforeEach
    void setUp() {
        sourceTag = Tag.builder().name("Source").build();
        sourceTag.setId(1L);

        targetTag = Tag.builder().name("Target").build();
        targetTag.setId(2L);
    }

    // ================= CREATE =================

    @Nested
    class CreateTagTests {

        @Test
        void createTag_shouldSaveNewTag_whenNameIsUnique() {
            when(tagRepository.existsByNameIgnoreCase("Armenian")).thenReturn(false);
            when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));

            Tag result = tagServiceAdmin.createTag(" Armenian ");

            assertThat(result.getName()).isEqualTo("Armenian");
            verify(tagRepository).save(any(Tag.class));
        }

        @Test
        void createTag_shouldThrow_whenTagExists() {
            when(tagRepository.existsByNameIgnoreCase("Armenian")).thenReturn(true);

            assertThatThrownBy(() -> tagServiceAdmin.createTag("Armenian"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tag already exists");

            verify(tagRepository, never()).save(any());
        }
    }

    @Nested
    class RenameTagTests {

        @Test
        void renameTag_shouldUpdateName_whenUnique() {
            when(tagRepository.findById(1L)).thenReturn(Optional.of(sourceTag));
            when(tagRepository.existsByNameIgnoreCase("NewName")).thenReturn(false);

            Tag updated = tagServiceAdmin.renameTag(1L, " NewName ");

            assertThat(updated.getName()).isEqualTo("NewName");
        }

        @Test
        void renameTag_shouldThrow_whenNameTaken() {
            when(tagRepository.findById(1L)).thenReturn(Optional.of(sourceTag));
            when(tagRepository.existsByNameIgnoreCase("Target")).thenReturn(true);

            assertThatThrownBy(() -> tagServiceAdmin.renameTag(1L, "Target"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class DeleteTagTests {

        @Test
        void deleteTag_shouldDelete_whenNotUsed() {
            when(tagRepository.findById(1L)).thenReturn(Optional.of(sourceTag));

            tagServiceAdmin.deleteTag(1L);

            verify(tagRepository).delete(sourceTag);
        }

        @Test
        void deleteTag_shouldThrow_whenUsedInPosts() {
            sourceTag.setPosts(Set.of(mock(Post.class)));
            when(tagRepository.findById(1L)).thenReturn(Optional.of(sourceTag));

            assertThatThrownBy(() -> tagServiceAdmin.deleteTag(1L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class MergeTagsTests {

        @Test
        void mergeTags_shouldThrow_whenSameId() {
            assertThatThrownBy(() -> tagServiceAdmin.mergeTags(1L, 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void mergeTags_shouldThrow_whenSourceNotFound() {
            when(tagRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tagServiceAdmin.mergeTags(1L, 2L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
