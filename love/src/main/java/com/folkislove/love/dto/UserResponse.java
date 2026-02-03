package com.folkislove.love.dto;

import com.folkislove.love.model.User.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private String username;
    private Role role;
}
