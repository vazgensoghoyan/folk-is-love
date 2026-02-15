package com.folkislove.love.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyTakenException extends AppException {
    
    public UsernameAlreadyTakenException(String username) {
        super(HttpStatus.CONFLICT, "Username already taken: " + username);
    }
}
