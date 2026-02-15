package com.folkislove.love.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyRegisteredException extends AppException {

    public EmailAlreadyRegisteredException(String email) {
        super(HttpStatus.CONFLICT, "Email already registered: " + email);
    }
}
