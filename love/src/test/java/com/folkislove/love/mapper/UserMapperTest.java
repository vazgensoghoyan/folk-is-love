package com.folkislove.love.mapper;

import com.folkislove.love.dto.UserResponse;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private static final String USERNAME = "folklover";
    private static final String BIO_TEXT = "bio text";
    private static final String TAG1_NAME = "Armenian";
    private static final String TAG2_NAME = "Slavic";

    // UserMapperImpl снегерирован с помощью MapStruct
    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void shouldMapUserToUserResponse() {
        Tag tag1 = Tag.builder().id(1L).name(TAG1_NAME).build();
        Tag tag2 = Tag.builder().id(2L).name(TAG2_NAME).build();

        User user = User.builder()
                .id(10L)
                .username(USERNAME)
                .bio(BIO_TEXT)
                .interests(Set.of(tag1, tag2))
                .build();

        UserResponse dto = mapper.toDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isEqualTo(USERNAME);
        assertThat(dto.getBio()).isEqualTo(BIO_TEXT);
        assertThat(dto.getInterests()).containsExactlyInAnyOrder(TAG1_NAME, TAG2_NAME);
    }

    @Test
    void shouldReturnEmptyListWhenInterestsIsNull() {
        User user = User.builder()
                .username(USERNAME)
                .bio(BIO_TEXT)
                .interests(null)
                .build();

        UserResponse dto = mapper.toDto(user);

        assertThat(dto.getInterests()).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenInterestsIsEmpty() {
        User user = User.builder()
                .username(USERNAME)
                .interests(Set.of())
                .build();

        UserResponse dto = mapper.toDto(user);

        assertThat(dto.getInterests()).isEmpty();
    }

    @Test
    void shouldMapNullFields() {
        User user = User.builder()
                .username(null)
                .bio(null)
                .interests(null)
                .build();

        UserResponse dto = mapper.toDto(user);

        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getBio()).isNull();
        assertThat(dto.getInterests()).isEmpty();
    }

}
