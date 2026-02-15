package com.folkislove.love.exception;

import org.springframework.http.HttpStatus;

public class TagInUseException extends AppException {
    
    public TagInUseException(String tagName) {
        super(HttpStatus.CONFLICT, "Cannot delete tag in use: " + tagName + ". Merge it instead.");
    }
}
