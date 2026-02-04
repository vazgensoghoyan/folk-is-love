package com.folkislove.love.mapper;

import com.folkislove.love.dto.EventResponse;
import com.folkislove.love.model.Event;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EventMapperTest {

    private static final String TITLE = "Folk Festival";
    private static final String DESCRIPTION = "A long description of the event";
    private static final String CITY = "Moscow";
    private static final String COUNTRY = "Russia";
    private static final String VENUE = "Red Square";
    private static final String LINK = "http://event.com";
    private static final String USERNAME = "folklover";
    private static final String TAG1 = "Music";
    private static final String TAG2 = "Dance";

    private final EventMapper mapper = Mappers.getMapper(EventMapper.class);

    @Test
    void shouldMapAllFields() {
        User author = User.builder().id(1L).username(USERNAME).build();
        Tag tag1 = Tag.builder().id(1L).name(TAG1).build();
        Tag tag2 = Tag.builder().id(2L).name(TAG2).build();

        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime createdAt = dateTime.minusDays(1);

        Event event = Event.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .dateTime(dateTime)
                .city(CITY)
                .country(COUNTRY)
                .venue(VENUE)
                .link(LINK)
                .author(author)
                .createdAt(createdAt)
                .tags(Set.of(tag1, tag2))
                .build();

        EventResponse dto = mapper.toDto(event);

        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo(TITLE);
        assertThat(dto.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(dto.getDateTime()).isEqualTo(dateTime);
        assertThat(dto.getCity()).isEqualTo(CITY);
        assertThat(dto.getCountry()).isEqualTo(COUNTRY);
        assertThat(dto.getVenue()).isEqualTo(VENUE);
        assertThat(dto.getLink()).isEqualTo(LINK);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getAuthorUsername()).isEqualTo(USERNAME);
        assertThat(dto.getTags()).containsExactlyInAnyOrder(TAG1, TAG2);
    }

    @Test
    void shouldReturnEmptyTagsWhenTagsNullOrEmpty() {
        Event withEmpty = Event.builder().tags(Set.of()).build();
        Event withNull = Event.builder().tags(null).build();

        assertThat(mapper.toDto(withEmpty).getTags()).isEmpty();
        assertThat(mapper.toDto(withNull).getTags()).isEmpty();
    }

    @Test
    void shouldHandleNullAuthorAndAuthorUsername() {
        Event noAuthor = Event.builder().author(null).build();
        Event nullUsername = Event.builder()
                .author(User.builder().id(1L).username(null).build())
                .build();

        assertThat(mapper.toDto(noAuthor).getAuthorUsername()).isNull();
        assertThat(mapper.toDto(nullUsername).getAuthorUsername()).isNull();
    }

    @Test
    void shouldMapNullFieldsSafely() {
        Event event = Event.builder().build();

        EventResponse dto = mapper.toDto(event);

        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getCity()).isNull();
        assertThat(dto.getCountry()).isNull();
        assertThat(dto.getVenue()).isNull();
        assertThat(dto.getLink()).isNull();
        assertThat(dto.getAuthorUsername()).isNull();
        assertThat(dto.getDateTime()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
        assertThat(dto.getTags()).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    void shouldReturnNullWhenEventIsNull(Event event) {
        assertThat(mapper.toDto(event)).isNull();
    }
}
