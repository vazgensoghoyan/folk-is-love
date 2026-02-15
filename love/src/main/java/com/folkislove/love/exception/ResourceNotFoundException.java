package com.folkislove.love.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String resource, Long id) {
        super(HttpStatus.NOT_FOUND, resource + " not found: " + id);
    }
}
