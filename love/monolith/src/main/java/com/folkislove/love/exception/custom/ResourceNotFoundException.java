package com.folkislove.love.exception.custom;

import org.springframework.http.HttpStatus;

import com.folkislove.love.exception.AppException;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String resource, Object id) {
        super(HttpStatus.NOT_FOUND, resource + " not found: " + id);
    }
}
