package com.folkislove.love.exception.custom;

import org.springframework.http.HttpStatus;

import com.folkislove.love.exception.AppException;

public class TagInUseException extends AppException {
    
    public TagInUseException(String tagName) {
        super(HttpStatus.CONFLICT, "Cannot delete tag in use: " + tagName + ". Merge it instead.");
    }
}
