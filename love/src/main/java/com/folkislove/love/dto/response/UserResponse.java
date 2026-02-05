package com.folkislove.love.dto.response;

import java.util.List;

import com.folkislove.love.model.User.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    
    private String username;

    private String email;

    private String bio;

    private Role role;

    private List<String> interests;

}
