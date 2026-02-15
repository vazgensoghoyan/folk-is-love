package com.folkislove.love.exception.custom;

import org.springframework.http.HttpStatus;

import com.folkislove.love.exception.AppException;

public class UsernameAlreadyTakenException extends AppException {
    
    public UsernameAlreadyTakenException(String username) {
        super(HttpStatus.CONFLICT, "Username already taken: " + username);
    }
}
