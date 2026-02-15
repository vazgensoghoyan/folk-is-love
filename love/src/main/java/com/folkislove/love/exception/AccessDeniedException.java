package com.folkislove.love.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends AppException {

    public AccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
