package com.folkislove.love.mapper;

import com.folkislove.love.dto.PostResponse;
import com.folkislove.love.model.Comment;
import com.folkislove.love.model.Post;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class PostMapperTest {

    private static final String TITLE = "Folk traditions";
    private static final String CONTENT = "Long description";
    private static final String USERNAME = "folklover";
    private static final String TAG1 = "Armenian";
    private static final String TAG2 = "Slavic";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 12, 0);

    private final PostMapper mapper = Mappers.getMapper(PostMapper.class);

    @Test
    void shouldMapPostToPostResponse() {
        User author = User.builder()
                .id(1L)
                .username(USERNAME)
                .build();

        Tag tag1 = Tag.builder().id(1L).name(TAG1).build();
        Tag tag2 = Tag.builder().id(2L).name(TAG2).build();

        Comment comment1 = Comment.builder().id(1L).build();
        Comment comment2 = Comment.builder().id(2L).build();

        Post post = Post.builder()
                .id(10L)
                .title(TITLE)
                .content(CONTENT)
                .author(author)
                .createdAt(CREATED_AT)
                .tags(Set.of(tag1, tag2))
                .comments(Set.of(comment1, comment2))
                .build();

        PostResponse dto = mapper.toDto(post);

        assertThat(dto).isNotNull();

        assertThat(dto)
                .extracting(
                        PostResponse::getTitle,
                        PostResponse::getContent,
                        PostResponse::getAuthorUsername,
                        PostResponse::getCreatedAt,
                        PostResponse::getCommentsCount
                )
                .containsExactly(TITLE, CONTENT, USERNAME, CREATED_AT, 2);

        assertThat(dto.getTags())
                .hasSize(2)
                .containsExactlyInAnyOrder(TAG1, TAG2);
    }

    @Test
    void shouldHandleEmptyCollections() {
        Post post = Post.builder()
                .title(TITLE)
                .content(CONTENT)
                .tags(Set.of())
                .comments(Set.of())
                .build();

        PostResponse dto = mapper.toDto(post);

        assertThat(dto.getTags()).isEmpty();
        assertThat(dto.getCommentsCount()).isEqualTo(0);
    }

    @Test
    void shouldHandleNullFields() {
        Post post = Post.builder().build();

        PostResponse dto = mapper.toDto(post);

        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getContent()).isNull();
        assertThat(dto.getAuthorUsername()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
        assertThat(dto.getTags()).isEmpty();
        assertThat(dto.getCommentsCount()).isEqualTo(0);
    }

    @Test
    void shouldMapNullUsernameWhenAuthorHasNoUsername() {
        User author = User.builder()
                .id(1L)
                .build();

        Post post = Post.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(author)
                .build();

        PostResponse dto = mapper.toDto(post);

        assertThat(dto.getAuthorUsername()).isNull();
    }

    @Test
    void tagsListShouldBeIndependentFromEntity() {
        Tag tag = Tag.builder().id(1L).name(TAG1).build();

        Post post = Post.builder()
                .tags(Set.of(tag))
                .build();

        PostResponse dto = mapper.toDto(post);

        assertThat(dto.getTags()).containsExactly(TAG1);
        assertThatThrownBy(() -> dto.getTags().add("Hack"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @NullSource
    void shouldReturnNullWhenPostIsNull(Post post) {
        PostResponse dto = mapper.toDto(post);
        assertThat(dto).isNull();
    }
}
