package com.folkislove.love.exception;

import org.springframework.http.HttpStatus;

public class TagAlreadyExistsException extends AppException {
    
    public TagAlreadyExistsException(String tagName) {
        super(HttpStatus.CONFLICT, "Tag already exists: " + tagName);
    }
}
