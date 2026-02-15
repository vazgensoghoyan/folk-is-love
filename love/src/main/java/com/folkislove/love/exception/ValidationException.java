package com.folkislove.love.exception;

import org.springframework.http.HttpStatus;

public abstract class ValidationException extends AppException {

    protected ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
