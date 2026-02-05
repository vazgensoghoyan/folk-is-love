package com.folkislove.love.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.folkislove.love.dto.response.CommentResponse;
import com.folkislove.love.model.Comment;
import com.folkislove.love.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorUsername", source = "author", qualifiedByName = "authorToUsername")
    @Mapping(target = "edited", source = "updatedAt", qualifiedByName = "updatedAtToEdited")
    CommentResponse toDto(Comment post);

    @Named("authorToUsername")
    default String authorToUsername(User author) {
        if (author == null) return null;
        return author.getUsername();
    }

    @Named("updatedAtToEdited")
    default boolean updatedAtToEdited(java.time.LocalDateTime updatedAt) {
        return updatedAt != null;
    }

}
