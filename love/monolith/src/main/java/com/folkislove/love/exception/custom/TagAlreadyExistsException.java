package com.folkislove.love.exception.custom;

import org.springframework.http.HttpStatus;

import com.folkislove.love.exception.AppException;

public class TagAlreadyExistsException extends AppException {
    
    public TagAlreadyExistsException(String tagName) {
        super(HttpStatus.CONFLICT, "Tag already exists: " + tagName);
    }
}
