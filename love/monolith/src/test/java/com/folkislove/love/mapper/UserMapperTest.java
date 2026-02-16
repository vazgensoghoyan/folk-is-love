package com.folkislove.love.mapper;

import com.folkislove.common.dto.response.UserResponse;
import com.folkislove.common.enums.Role;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class UserMapperTest {

    private static final String USERNAME = "folklover";
    private static final String EMAIL = "folk@test.com";
    private static final String BIO_TEXT = "bio text";
    private static final String TAG1_NAME = "Armenian";
    private static final String TAG2_NAME = "Slavic";

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserToUserResponse() {
        Tag tag1 = Tag.builder().id(1L).name(TAG1_NAME).build();
        Tag tag2 = Tag.builder().id(2L).name(TAG2_NAME).build();

        User user = User.builder()
                .id(10L)
                .username(USERNAME)
                .email(EMAIL)
                .bio(BIO_TEXT)
                .role(Role.ADMIN)
                .interests(Set.of(tag1, tag2))
                .build();

        UserResponse dto = mapper.toDto(user);

        assertThat(dto).isNotNull();

        assertThat(dto)
                .extracting(
                        UserResponse::getUsername,
                        UserResponse::getEmail,
                        UserResponse::getBio,
                        UserResponse::getRole
                )
                .containsExactly(USERNAME, EMAIL, BIO_TEXT, Role.ADMIN);

        assertThat(dto.getInterests())
                .hasSize(2)
                .containsExactlyInAnyOrder(TAG1_NAME, TAG2_NAME);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturnEmptyListWhenInterestsNullOrEmpty(Set<Tag> interests) {
        User user = User.builder()
                .username(USERNAME)
                .email(EMAIL)
                .bio(BIO_TEXT)
                .interests(interests)
                .build();

        UserResponse dto = mapper.toDto(user);

        assertThat(dto.getInterests()).isEmpty();
    }

    @Test
    void shouldMapNullFields() {
        User user = User.builder().build();

        UserResponse dto = mapper.toDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getBio()).isNull();
        assertThat(dto.getInterests()).isEmpty();
    }

    @Test
    void interestsListShouldBeImmutable() {
        Tag tag = Tag.builder().id(1L).name(TAG1_NAME).build();

        User user = User.builder()
                .username(USERNAME)
                .email(EMAIL)
                .interests(Set.of(tag))
                .build();

        UserResponse dto = mapper.toDto(user);

        assertThat(dto.getInterests()).containsExactly(TAG1_NAME);
        assertThatThrownBy(() -> dto.getInterests().add("Hack"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldReturnNullWhenUserIsNull() {
        UserResponse dto = mapper.toDto(null);
        assertThat(dto).isNull();
    }
}
