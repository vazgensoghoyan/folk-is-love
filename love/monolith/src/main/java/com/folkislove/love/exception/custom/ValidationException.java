package com.folkislove.love.exception.custom;

import org.springframework.http.HttpStatus;

import com.folkislove.love.exception.AppException;

public abstract class ValidationException extends AppException {

    protected ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
