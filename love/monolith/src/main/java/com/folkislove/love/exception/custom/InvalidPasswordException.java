package com.folkislove.love.exception.custom;

public class InvalidPasswordException extends ValidationException {

    public InvalidPasswordException(String message) {
        super(message);
    }
}
