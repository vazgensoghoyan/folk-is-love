package com.folkislove.love.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends AppException {

    public AuthorizationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
