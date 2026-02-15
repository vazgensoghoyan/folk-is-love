package com.folkislove.love.mapper;

import com.folkislove.love.dto.response.UserResponse;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "interests", source = "interests", qualifiedByName = "tagsToNames")
    UserResponse toDto(User user);

    @Named("tagsToNames")
    default List<String> tagsToNames(Set<Tag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                   .map(Tag::getName)
                   .toList();
    }
}
