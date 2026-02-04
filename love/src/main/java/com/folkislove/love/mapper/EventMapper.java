package com.folkislove.love.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.folkislove.love.dto.EventResponse;
import com.folkislove.love.model.Event;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsToNames")
    @Mapping(target = "authorUsername", source = "author", qualifiedByName = "authorToUsername")
    EventResponse toDto(Event event);

    @Named("authorToUsername")
    default String authorToUsername(User author) {
        if (author == null) return null;
        return author.getUsername();
    }

    @Named("tagsToNames")
    default List<String> tagsToNames(Set<Tag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                   .map(Tag::getName)
                   .toList();
    }
}
