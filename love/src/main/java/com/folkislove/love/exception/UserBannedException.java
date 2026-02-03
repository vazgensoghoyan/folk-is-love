package com.folkislove.love.exception;

public class UserBannedException extends RuntimeException {
    public UserBannedException(String username) {
        super("User '" + username + "' is banned");
    }
}
