package com.folkislove.love.exception.custom;

import org.springframework.http.HttpStatus;
import com.folkislove.love.exception.AppException;

public class InvalidEventDateException extends AppException {

    public InvalidEventDateException() {
        super(HttpStatus.BAD_REQUEST, "Event date and time must be in the future");
    }
}
