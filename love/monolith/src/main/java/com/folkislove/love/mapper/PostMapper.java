package com.folkislove.love.mapper;

import com.folkislove.common.dto.response.PostResponse;
import com.folkislove.love.model.Comment;
import com.folkislove.love.model.Post;
import com.folkislove.love.model.Tag;
import com.folkislove.love.model.User;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "authorUsername", source = "author", qualifiedByName = "authorToUsername")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsToNames")
    @Mapping(target = "commentsCount", source = "comments", qualifiedByName = "commentsToCount")
    PostResponse toDto(Post post);

    @Named("tagsToNames")
    default List<String> tagsToNames(Set<Tag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                   .map(Tag::getName)
                   .toList();
    }

    @Named("authorToUsername")
    default String authorToUsername(User author) {
        if (author == null) return null;
        return author.getUsername();
    }

    @Named("commentsToCount")
    default Integer commentsToCount(Set<Comment> comments) {
        if (comments == null) return 0;
        return comments.size();
    }
}
