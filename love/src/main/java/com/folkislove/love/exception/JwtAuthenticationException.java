package com.folkislove.love.exception;

import org.springframework.http.HttpStatus;

public class JwtAuthenticationException extends AppException {

    public JwtAuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
