package com.folkislove.love.dto;

import java.util.List;

import com.folkislove.love.model.User.Role;

import lombok.Data;

@Data
public class UserResponse {
    private String username;
    private String email;
    private String bio;
    private Role role;
    private List<String> interests;
}
