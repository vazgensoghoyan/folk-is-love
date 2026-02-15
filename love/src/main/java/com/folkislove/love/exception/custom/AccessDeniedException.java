package com.folkislove.love.exception.custom;

import org.springframework.http.HttpStatus;

import com.folkislove.love.exception.AppException;

public class AccessDeniedException extends AppException {

    public AccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
