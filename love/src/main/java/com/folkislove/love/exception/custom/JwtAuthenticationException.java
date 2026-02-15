package com.folkislove.love.exception.custom;

import org.springframework.http.HttpStatus;

import com.folkislove.love.exception.AppException;

public class JwtAuthenticationException extends AppException {

    public JwtAuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
