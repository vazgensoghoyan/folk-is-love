package com.folkislove.love.mapper;

import com.folkislove.love.dto.CommentResponse;
import com.folkislove.love.model.Comment;
import com.folkislove.love.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private static final String CONTENT = "Nice post!";
    private static final String USERNAME = "folklover";

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void shouldMapCommentToResponse() {
        User author = User.builder()
                .id(1L)
                .username(USERNAME)
                .build();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = createdAt.plusMinutes(5);

        Comment comment = Comment.builder()
                .id(10L)
                .content(CONTENT)
                .author(author)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        CommentResponse dto = mapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
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

        assertThat(dto.isEdited()).isFalse();
    }

    @Test
    void shouldHandleNullAuthor() {
        Comment comment = Comment.builder()
                .content(CONTENT)
                .author(null)
                .build();

        CommentResponse dto = mapper.toDto(comment);

        assertThat(dto.getAuthorUsername()).isNull();
    }

    @ParameterizedTest
    @NullSource
    void shouldReturnNullWhenCommentIsNull(Comment comment) {
        CommentResponse dto = mapper.toDto(comment);
        assertThat(dto).isNull();
    }
}
