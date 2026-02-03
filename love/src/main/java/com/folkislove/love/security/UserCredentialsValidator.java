package com.folkislove.love.security;

import org.springframework.stereotype.Component;
import com.folkislove.love.exception.InvalidCredentialsException;
import com.folkislove.love.exception.WeakPasswordException;

@Component
public class UserCredentialsValidator {

    public void validateUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            throw new InvalidCredentialsException();
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < 10) {
            throw new WeakPasswordException("Password must be at least 10 characters long");
        }

        if (password.contains(" ")) {
            throw new WeakPasswordException("Password must not contain spaces");
        }

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        if (!hasLower) {
            throw new WeakPasswordException("Password must contain at least one lowercase letter");
        }
        if (!hasUpper) {
            throw new WeakPasswordException("Password must contain at least one uppercase letter");
        }
        if (!hasDigit) {
            throw new WeakPasswordException("Password must contain at least one digit");
        }
        if (!hasSpecial) {
            throw new WeakPasswordException("Password must contain at least one special character");
        }
    }
}
