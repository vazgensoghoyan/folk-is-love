package com.folkislove.love.exception.custom;

public class InvalidEmailException extends ValidationException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
