package com.folkislove.love.mapper;

import com.folkislove.love.dto.UserResponse;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    // UserMapperImpl снегерирован с помощью MapStruct
    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void shouldMapUserToUserResponse() {
        Tag tag1 = Tag.builder().id(1L).name("Armenian").build();
        Tag tag2 = Tag.builder().id(2L).name("Slavic").build();

        User user = User.builder()
                .id(10L)
                .username("folklover")
                .bio("Bio text")
                .interests(Set.of(tag1, tag2))
                .build();

        UserResponse dto = mapper.toDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isEqualTo("folklover");
        assertThat(dto.getBio()).isEqualTo("Bio text");
        assertThat(dto.getInterests()).containsExactlyInAnyOrder("Armenian", "Slavic");
    }
}
