package com.folkislove.love.mapper;

import com.folkislove.love.dto.response.CommentResponse;
import com.folkislove.love.model.Comment;
import com.folkislove.love.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private static final Long COMMENT_ID = 10L;
    private static final String CONTENT = "Nice post!";
    private static final String USERNAME = "folklover";

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void shouldMapAllFieldsWhenCommentIsEdited() {
        User author = User.builder()
                .id(1L)
                .username(USERNAME)
                .build();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = createdAt.plusMinutes(5);

        Comment comment = Comment.builder()
                .id(COMMENT_ID)
                .content(CONTENT)
                .author(author)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        CommentResponse dto = mapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(COMMENT_ID);
        assertThat(dto.getContent()).isEqualTo(CONTENT);
        assertThat(dto.getAuthorUsername()).isEqualTo(USERNAME);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.isEdited()).isTrue();
    }

    @Test
    void shouldSetEditedFalseWhenUpdatedAtIsNull() {
        Comment comment = Comment.builder()
                .id(1L)
                .content(CONTENT)
                .updatedAt(null)
                .build();

        CommentResponse dto = mapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.isEdited()).isFalse();
    }

    @Test
    void shouldReturnNullAuthorUsernameWhenAuthorIsNull() {
        Comment comment = Comment.builder()
                .id(1L)
                .content(CONTENT)
                .build();

        CommentResponse dto = mapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getAuthorUsername()).isNull();
    }

    @Test
    void shouldReturnNullAuthorUsernameWhenAuthorUsernameIsNull() {
        User author = User.builder()
                .id(1L)
                .username(null)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .content(CONTENT)
                .author(author)
                .build();

        CommentResponse dto = mapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getAuthorUsername()).isNull();
    }

    @Test
    void shouldMapNullFieldsSafely() {
        Comment comment = Comment.builder().build();

        CommentResponse dto = mapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getContent()).isNull();
        assertThat(dto.getAuthorUsername()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
        assertThat(dto.isEdited()).isFalse();
    }

    @ParameterizedTest
    @NullSource
    void shouldReturnNullWhenCommentIsNull(Comment comment) {
        CommentResponse dto = mapper.toDto(comment);
        assertThat(dto).isNull();
    }
}
