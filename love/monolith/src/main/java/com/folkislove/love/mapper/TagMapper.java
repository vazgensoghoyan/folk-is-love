package com.folkislove.love.mapper;

import org.mapstruct.Mapper;

import com.folkislove.common.dto.response.TagResponse;
import com.folkislove.love.model.Tag;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagResponse toDto(Tag tag);
}
