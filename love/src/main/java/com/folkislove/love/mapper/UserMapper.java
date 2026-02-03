package com.folkislove.love.mapper;

import com.folkislove.love.dto.UserResponse;
import com.folkislove.love.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
}
